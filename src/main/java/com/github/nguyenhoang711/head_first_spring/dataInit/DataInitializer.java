package com.github.nguyenhoang711.head_first_spring.dataInit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.github.nguyenhoang711.head_first_spring.entity.Permission;
import com.github.nguyenhoang711.head_first_spring.entity.Role;
import com.github.nguyenhoang711.head_first_spring.entity.User;
import com.github.nguyenhoang711.head_first_spring.repository.PermissionRepository;
import com.github.nguyenhoang711.head_first_spring.repository.RoleRepository;
import com.github.nguyenhoang711.head_first_spring.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataInitializer {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostConstruct
  @Transactional
  // func sẽ được gọi khi ứng dụng khởi động
  public void initialize() {
    // Khởi tạo permissions
    Permission readPermission = createPermissionIfNotFound("READ");
    Permission writePermission = createPermissionIfNotFound("WRITE");
    Permission deletePermission = createPermissionIfNotFound("DELETE");

    // Khởi tạo roles
    Role adminRole = createRoleIfNotFound("MAM1", new HashSet<>(Arrays.asList(
        readPermission, writePermission, deletePermission)));

    Role userRole = createRoleIfNotFound("MAM3", new HashSet<>(Arrays.asList(
        readPermission)));

    // Khởi tạo acc mâm1
    if (userRepository.findByUsernameAndDeletedAtIsNull("admin") == null) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword(passwordEncoder.encode("123456"));
      admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
      userRepository.save(admin);
    }

    // Khởi tạo acc mâm3
    if (userRepository.findByUsernameAndDeletedAtIsNull("user") == null) {
      User user = new User();
      user.setUsername("user");
      user.setPassword(passwordEncoder.encode("123456"));
      user.setRoles(new HashSet<>(Arrays.asList(userRole)));
      userRepository.save(user);
    }
  }

  @Transactional
  public Permission createPermissionIfNotFound(String name) {
    Permission permission = permissionRepository.findByName(name);
    if (permission == null) {
      permission = new Permission();
      permission.setName(name);
      permissionRepository.save(permission);
    }
    return permission;
  }

  @Transactional
  public Role createRoleIfNotFound(String name, Set<Permission> permissions) {
    Role role = roleRepository.findByName(name);
    if (role == null) {
      role = new Role();
      role.setName(name);
      role.setPermissions(permissions);
      roleRepository.save(role);
    }
    return role;
  }
}
