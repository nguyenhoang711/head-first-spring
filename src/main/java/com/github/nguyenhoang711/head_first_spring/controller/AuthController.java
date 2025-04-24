package com.github.nguyenhoang711.head_first_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.nguyenhoang711.head_first_spring.dto.request.AuthDto;
import com.github.nguyenhoang711.head_first_spring.service.JwtAuthService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "API Auth")
@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  private JwtAuthService jwtAuthService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody AuthDto authRequest) {
    return ResponseEntity.ok(jwtAuthService.register(authRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthDto authRequest) {
    return ResponseEntity.ok(jwtAuthService.login(authRequest));
  }
}
