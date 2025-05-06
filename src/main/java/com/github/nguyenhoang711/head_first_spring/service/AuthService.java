package com.github.nguyenhoang711.head_first_spring.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.github.nguyenhoang711.head_first_spring.constant.OtpType;
import com.github.nguyenhoang711.head_first_spring.dto.request.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.nguyenhoang711.head_first_spring.constant.CommonMsg;
import com.github.nguyenhoang711.head_first_spring.dto.response.BaseResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.LoginResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.UserResponse;
import com.github.nguyenhoang711.head_first_spring.entity.Role;
import com.github.nguyenhoang711.head_first_spring.entity.User;
import com.github.nguyenhoang711.head_first_spring.repository.RoleRepository;
import com.github.nguyenhoang711.head_first_spring.repository.UserRepository;
import com.github.nguyenhoang711.head_first_spring.security.JwtTokenProvider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private EmailService emailService;

  @Autowired
  private RedisService redisService;

  @Autowired
  private OtpService otpService;

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> register(RegisterDto registerDto) {
    if (userRepository.findByUsernameAndDeletedAtIsNull(registerDto.getUsername()) != null) {
      return BaseResponse.error(CommonMsg.DUPLICATE_USERNAME);
    }

    String otp = String.format("%06d", new Random().nextInt(999999));

    try {
      otpService.saveOtp(registerDto.getUsername(), otp, 300, OtpType.REGISTRATION);
      redisService.savePendingRegistration(registerDto, 300);

      try {
        emailService.sendOtpEmail(registerDto.getEmail(), otp, OtpType.REGISTRATION);
      } catch (Exception e) {
        throw new RuntimeException(CommonMsg.SEND_OTP_FAILED + ": " + e.getMessage());
      }

      return BaseResponse.success(CommonMsg.SEND_OTP_SUCCESS, null);
    } catch (Exception e) {
      // Rollback thủ công cho Redis
      otpService.deleteOtp(registerDto.getUsername());
      redisService.deletePendingRegistration(registerDto.getUsername());

      return BaseResponse.error(CommonMsg.SEND_OTP_FAILED);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public BaseResponse<?> verifyRegister(VerifyOtpDto verifyOtpDto) {
    // 1. get pending register
    RegisterDto registerDto = redisService.getPendingRegistration(verifyOtpDto.getUsername());
    if (registerDto == null) {
      return BaseResponse.error(CommonMsg.USERNAME_IS_NOT_CORRECT);
    }
    // 2. check otp correct or not
    if (!otpService.validateOtp(verifyOtpDto.getUsername(), verifyOtpDto.getOtp(), OtpType.REGISTRATION)) {
      return BaseResponse.error(CommonMsg.OTP_NOT_CORRECT);
    }

    // 3. check duplicate username
    if (userRepository.findByUsernameAndDeletedAtIsNull(verifyOtpDto.getUsername()) != null) {
      return BaseResponse.error(CommonMsg.DUPLICATE_USERNAME);
    }

    // 4. create user account
    User newUser = new User();
    newUser.setUsername(verifyOtpDto.getUsername());
    newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
    newUser.setEmail(registerDto.getEmail());

    Set<Role> roles = new HashSet<>();
    Role userRole = roleRepository.findByName("MAM3");
    if (userRole == null) {
      userRole = new Role();
      userRole.setName("MAM3");
      roleRepository.save(userRole);
    }

    roles.add(userRole);
    newUser.setRoles(roles);
    userRepository.save(newUser);
    // 5. delete pending registration
    redisService.deletePendingRegistration(verifyOtpDto.getUsername());

    UserResponse userResponse = mapToUserResponse(newUser);
    return BaseResponse.success(CommonMsg.REGISTER_SUCCESS, userResponse);
  }

  public BaseResponse<?> login(LoginDto loginRequest) {
    User user = userRepository.findByUsernameAndDeletedAtIsNull(loginRequest.getUsername());
    if (user == null) {
      return BaseResponse.error(CommonMsg.USERNAME_IS_NOT_CORRECT);
    }

    if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      return BaseResponse.error(CommonMsg.LOGIN_FAILED);
    }

    String otp = String.format("%06d", new Random().nextInt(999999));
    try {
      otpService.saveOtp(loginRequest.getUsername(), otp, 300, OtpType.LOGIN);

      try {
        emailService.sendOtpEmail(user.getEmail(), otp, OtpType.LOGIN);
      } catch (Exception e) {
        throw new RuntimeException(CommonMsg.SEND_OTP_FAILED + ": " + e.getMessage());
      }
    } catch (Exception e) {
      // Rollback thủ công cho Redis
      otpService.deleteOtp(loginRequest.getUsername());
      return BaseResponse.error(CommonMsg.SEND_OTP_FAILED);
    }

    return BaseResponse.success(CommonMsg.SEND_OTP_SUCCESS, null);
  }

  public BaseResponse<?> verifyLogin(VerifyOtpDto verifyOtpDto) {
    User user = userRepository.findByUsernameAndDeletedAtIsNull(verifyOtpDto.getUsername());
    if (!otpService.validateOtp(verifyOtpDto.getUsername(), verifyOtpDto.getOtp(), OtpType.LOGIN)) {
      return BaseResponse.error(CommonMsg.OTP_NOT_CORRECT);
    }

    String token = jwtTokenProvider.createToken(user);

    UserResponse userResponse = mapToUserResponse(user);
    redisService.saveUserInfo(user.getUsername(), userResponse);

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setUsername(user.getUsername());
    loginResponse.setToken(token);

    return BaseResponse.success(CommonMsg.LOGIN_SUCCESS, loginResponse);
  }

  public BaseResponse<?> logout(String token) {
    // 1. check token valid or not
    token = token.replace("Bearer ", "");
    if(!jwtTokenProvider.validateToken(token)) {
      return BaseResponse.error(CommonMsg.INVALID_TOKEN);
    }

    // get username from token to delete cache
    String username = jwtTokenProvider.getUsername(token);

    // 3. add token to blacklist
    try {
      Claims claims = Jwts.parser().setSigningKey(jwtTokenProvider.getSecretKey()).parseClaimsJws(token).getBody();
      long ttlSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
      if (ttlSeconds > 0) {
        redisService.blacklistToken(token, ttlSeconds);
      }
    } catch (JwtException e) {
      log.error("Error parsing token for logout: {}", token, e);
      return BaseResponse.error(CommonMsg.INVALID_TOKEN);
    }
    // 4. remove token and userinfo from cache
    redisService.deleteToken(username);
    redisService.deleteUserInfo(username);

    return BaseResponse.success(CommonMsg.LOGOUT_SUCCESS, null);
  }


  public UserResponse mapToUserResponse(User user) {
    UserResponse userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());
    Set<String> rolesSet = new HashSet<>();
    for (Role role : user.getRoles()) {
      rolesSet.add(role.getName());
    }
    userResponse.setRoles(new ArrayList<>(rolesSet));

    Set<String> permissionsSet = new HashSet<>();
    for (Role role : user.getRoles()) {
      role.getPermissions().forEach(permission -> permissionsSet.add(permission.getName()));
    }
    userResponse.setPermissions(new ArrayList<>(permissionsSet));

    return userResponse;
  }

  public BaseResponse<?> changePassword(ChangePasswordDto changePasswordDto){
    // 1. handle old password and new password
    String oldPassword = changePasswordDto.getOldPassword();
    String newPassword = changePasswordDto.getNewPassword();
    if (oldPassword == null || oldPassword.trim().isEmpty()) {
      return BaseResponse.error(CommonMsg.PASSWORD_IS_NOT_BLANK);
    }
    if (newPassword == null || newPassword.trim().isEmpty()) {
      return BaseResponse.error(CommonMsg.PASSWORD_IS_NOT_BLANK);
    }
    // 2. authentication
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findByUsernameAndDeletedAtIsNull(authentication.getName());
    if (user == null) {
      return BaseResponse.error(CommonMsg.USER_NOT_FOUND);
    }
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
      return BaseResponse.error(CommonMsg.OLD_PASSWORD_IS_NOT_CORRECT);
    }
    // 3. check old password same with new one
    if (oldPassword.equals(newPassword)) {
      return BaseResponse.error(CommonMsg.NEW_PASSWORD_IS_NOT_DIFFERENT);
    }

    redisService.deleteToken(user.getUsername());
    redisService.deleteUserInfo(user.getUsername());
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    return BaseResponse.success(CommonMsg.CHANGE_PASSWORD_SUCCESS, null);
  }

  public BaseResponse<?> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
    // 1. check user exist or not
    User user = userRepository.findByUsernameAndDeletedAtIsNull(forgotPasswordDto.getUsername());
    if (user == null) {
      return BaseResponse.error(CommonMsg.USER_NOT_FOUND);
    }
    // 2. send OTP
    String otp = String.format("%06d", new Random().nextInt(999999));
    try {
      otpService.saveOtp(forgotPasswordDto.getUsername(), otp, 300, OtpType.RESET_PASSWORD);

      try {
        emailService.sendOtpEmail(user.getEmail(), otp, OtpType.RESET_PASSWORD);
      } catch (Exception e) {
        throw new RuntimeException(CommonMsg.SEND_OTP_FAILED + ": " + e.getMessage());
      }
    } catch (Exception e) {
      // Rollback thủ công cho Redis
      otpService.deleteOtp(forgotPasswordDto.getUsername());
      return BaseResponse.error(CommonMsg.SEND_OTP_FAILED);
    }

    return BaseResponse.success(CommonMsg.SEND_OTP_SUCCESS, null);
  }

  public BaseResponse<?> resetPassword(ResetPasswordDto resetPasswordDto) {
    User user = userRepository.findByUsernameAndDeletedAtIsNull(resetPasswordDto.getUsername());
    if (user == null) {
      return BaseResponse.error(CommonMsg.USER_NOT_FOUND);
    }
    if (!otpService.validateOtp(resetPasswordDto.getUsername(), resetPasswordDto.getOtp(), OtpType.RESET_PASSWORD)) {
      return BaseResponse.error(CommonMsg.OTP_NOT_CORRECT);
    }
    redisService.deleteToken(resetPasswordDto.getUsername());
    redisService.deleteUserInfo(resetPasswordDto.getUsername());

    user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
    userRepository.save(user);

    return BaseResponse.success(CommonMsg.RESET_PASSWORD_SUCCESS, null);
  }
}
