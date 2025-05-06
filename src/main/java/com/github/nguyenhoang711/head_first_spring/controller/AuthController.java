package com.github.nguyenhoang711.head_first_spring.controller;

import com.github.nguyenhoang711.head_first_spring.dto.request.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.nguyenhoang711.head_first_spring.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "API Auth")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
  @Autowired
  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterDto authRequest) {
    return ResponseEntity.ok(authService.register(authRequest));
  }

  @PostMapping("/verify-register")
  public ResponseEntity<?> verifyRegister(@RequestBody VerifyOtpDto request) {
    return ResponseEntity.ok(authService.verifyRegister(request));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto authRequest) {
    return ResponseEntity.ok(authService.login(authRequest));
  }

  @PostMapping("/verify-login")
  public ResponseEntity<?> verifyLogin(@RequestBody VerifyOtpDto verifyOtpDto) {
    return ResponseEntity.ok(authService.verifyLogin(verifyOtpDto));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    return ResponseEntity.ok(authService.logout(authHeader));
  }

  @PutMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto request) {
    return ResponseEntity.ok(authService.changePassword(request));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto request) {
    return ResponseEntity.ok(authService.forgotPassword(request));
  }

  @PutMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto request) {
    return ResponseEntity.ok(authService.resetPassword(request));
  }
}
