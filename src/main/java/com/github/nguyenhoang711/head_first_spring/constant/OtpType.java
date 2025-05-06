package com.github.nguyenhoang711.head_first_spring.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum OtpType {
    RESET_PASSWORD("reset-password:", 2 * 60), // 2 phút
    REGISTRATION("registration:", 5 * 60), // 5 phút
    LOGIN("login:", 5 * 60); // 5 phút

    String name;
    int expireTimeInSeconds;
}
