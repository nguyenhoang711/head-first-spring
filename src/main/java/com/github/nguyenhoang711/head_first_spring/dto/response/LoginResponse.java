package com.github.nguyenhoang711.head_first_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
  private String token;
  private String username;
}
