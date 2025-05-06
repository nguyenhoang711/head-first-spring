package com.github.nguyenhoang711.head_first_spring.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
  private String username;
  private String password;
}
