package com.github.nguyenhoang711.head_first_spring.dto.request;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
  private Set<String> roles;
}
