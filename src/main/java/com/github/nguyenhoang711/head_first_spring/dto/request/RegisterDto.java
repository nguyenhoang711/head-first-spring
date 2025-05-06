package com.github.nguyenhoang711.head_first_spring.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDto {
    private String username;
    private String password;
    private String email;
}
