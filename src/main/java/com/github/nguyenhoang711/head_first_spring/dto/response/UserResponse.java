package com.github.nguyenhoang711.head_first_spring.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
  private Long id;
  private String username;
  private List<String> roles;
  private List<String> permissions;
}
