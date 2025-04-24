package com.github.nguyenhoang711.head_first_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.nguyenhoang711.head_first_spring.dto.request.UserDto;
import com.github.nguyenhoang711.head_first_spring.dto.response.BaseResponse;
import com.github.nguyenhoang711.head_first_spring.dto.response.UserResponse;
import com.github.nguyenhoang711.head_first_spring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User", description = "API User")
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Operation(summary = "Lấy danh sách người dùng")
  @GetMapping("")
  @PreAuthorize("hasAuthority('READ')")
  public ResponseEntity<?> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @Operation(summary = "Cập nhật thông tin người dùng")
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('UPDATE')")
  public ResponseEntity<BaseResponse<UserResponse>> updateUser(@PathVariable(name = "id") Long id,
      @RequestBody UserDto userDto) {
    log.info("Received update request for user id: {}, roles: {}", id,
        userDto != null ? userDto.getRoles() : "null");
    try {
      String json = objectMapper.writeValueAsString(userDto);
      log.info("UserDto raw JSON: {}", json);
    } catch (Exception e) {
      log.error("Error serializing UserDto", e);
    }
    return ResponseEntity.ok(userService.updateUser(id, userDto));
  }

  @Operation(summary = "Xóa người dùng")
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('DELETE')")
  public ResponseEntity<?> deleteUser(@PathVariable(name = "id") Long id) {
    return ResponseEntity.ok(userService.deleteUser(id));
  }

}
