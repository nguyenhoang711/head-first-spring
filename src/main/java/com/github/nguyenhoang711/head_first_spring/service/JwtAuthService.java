package com.github.nguyenhoang711.head_first_spring.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.nguyenhoang711.head_first_spring.constant.CommonMsg;
import com.github.nguyenhoang711.head_first_spring.dto.request.AuthDto;
import com.github.nguyenhoang711.head_first_spring.dto.response.BaseResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.LoginResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.UserResponse;
import com.github.nguyenhoang711.head_first_spring.entity.Role;
import com.github.nguyenhoang711.head_first_spring.entity.User;
import com.github.nguyenhoang711.head_first_spring.repository.RoleRepository;
import com.github.nguyenhoang711.head_first_spring.repository.UserRepository;
import com.github.nguyenhoang711.head_first_spring.security.JwtTokenProvider;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class JwtAuthService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  public BaseResponse<?> register(AuthDto authRequest) {
    if (userRepository.findByUsernameAndDeletedAtIsNull(authRequest.getUsername()) != null) {
      return BaseResponse.error(CommonMsg.DUPLICATE_USERNAME);
    }

    User newUser = new User();
    newUser.setUsername(authRequest.getUsername());
    newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
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

    UserResponse userResponse = mapToUserResponse(newUser);

    return BaseResponse.success(CommonMsg.REGISTER_SUCCESS, userResponse);
  }

  public BaseResponse<?> login(AuthDto authRequest) {
    User user = userRepository.findByUsernameAndDeletedAtIsNull(authRequest.getUsername());
    if (user == null || !passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
      return BaseResponse.error(CommonMsg.LOGIN_FAILED);
    }

    String token = jwtTokenProvider.createToken(user);

    LoginResponse loginResponse = new LoginResponse();
    loginResponse.setUsername(user.getUsername());
    loginResponse.setToken(token);

    return BaseResponse.success(CommonMsg.LOGIN_SUCCESS, loginResponse);
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
}
