package com.github.nguyenhoang711.head_first_spring.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
    private String username;
    private String otp;
    private String newPassword;
}