package com.github.nguyenhoang711.head_first_spring.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.nguyenhoang711.head_first_spring.constant.CommonMsg;
import com.github.nguyenhoang711.head_first_spring.dto.request.UserDto;
import com.github.nguyenhoang711.head_first_spring.dto.response.BaseResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.UserResponse;
import com.github.nguyenhoang711.head_first_spring.entity.Permission;
import com.github.nguyenhoang711.head_first_spring.entity.Role;
import com.github.nguyenhoang711.head_first_spring.entity.User;
import com.github.nguyenhoang711.head_first_spring.repository.RoleRepository;
import com.github.nguyenhoang711.head_first_spring.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  public BaseResponse<List<UserResponse>> getUsers() {
    List<User> users = userRepository.findAllByDeletedAtIsNull();
    List<UserResponse> userResponses = users.stream()
        .map(this::mapToUserResponse)
        .collect(Collectors.toList());

    return BaseResponse.success(CommonMsg.SUCCESS, userResponses);
  }

  @Transactional
  public BaseResponse<UserResponse> updateUser(Long id, UserDto userDto) {
    Optional<User> userOptional = userRepository.findByIdAndDeletedAtIsNull(id);
    if (!userOptional.isPresent()) {
      log.error("User not found or has been deleted with id: {}", id);
      throw new EntityNotFoundException("User not found or has been deleted with id: " + id);
    }

    User user = userOptional.get();

    if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
      Set<Role> roles = userDto.getRoles().stream()
          .map(name -> {
            Role role = roleRepository.findByName(name);
            if (role == null) {
              throw new IllegalArgumentException("Role not found: " + name);
            }
            return role;
          })
          .collect(Collectors.toSet());
      user.getRoles().clear();
      user.getRoles().addAll(roles);
    } else {
      log.warn("No roles provided in request");
    }

    user = userRepository.save(user);

    user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found after save"));

    UserResponse userResponse = mapToUserResponse(user);
    log.info("UserResponse created: {}", userResponse.getPermissions());

    return BaseResponse.success(CommonMsg.SUCCESS, userResponse);
  }

  public BaseResponse<?> deleteUser(Long id) {
    Optional<User> userOptional = userRepository.findByIdAndDeletedAtIsNull(id);
    if (!userOptional.isPresent()) {
      throw new EntityNotFoundException("User not found or has been deleted with id: " + id);
    }

    User user = userOptional.get();
    user.setDeletedAt(java.time.LocalDateTime.now());
    userRepository.save(user);
    return BaseResponse.success(CommonMsg.SUCCESS, CommonMsg.DELETE_USER_SUCCESS);
  }

  private UserResponse mapToUserResponse(User user) {
    UserResponse userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setUsername(user.getUsername());

    // Thêm thông tin về roles
    List<String> roleNames = new ArrayList<>();
    Map<String, List<String>> rolePermissionsMap = new HashMap<>();

    for (Role role : user.getRoles()) {
      String roleName = role.getName();
      roleNames.add(roleName);

      List<String> permissionNames = role.getPermissions().stream()
          .map(Permission::getName)
          .collect(Collectors.toList());
      rolePermissionsMap.put(roleName, permissionNames);
    }

    userResponse.setRoles(roleNames);

    Set<String> allPermissions = new HashSet<>();
    user.getRoles()
        .forEach(role -> role.getPermissions().forEach(permission -> allPermissions.add(permission.getName())));
    userResponse.setPermissions(new ArrayList<>(allPermissions));

    return userResponse;
  }
}
