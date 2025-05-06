package com.github.nguyenhoang711.head_first_spring.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    public String oldPassword;
    public String newPassword;
}
