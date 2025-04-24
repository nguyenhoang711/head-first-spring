package com.github.nguyenhoang711.head_first_spring.constant;

import lombok.Getter;

@Getter
public enum CommonMsg {
  // Success
  SUCCESS(0, "Thành công"),

  // Error
  FAILED(-1, "Không thành công"),

  // Auth
  REGISTER_SUCCESS(1000, "Đăng ký thành công"),
  LOGIN_SUCCESS(1001, "Đăng nhập thành công"),
  LOGIN_FAILED(4001, "Tài khoản hoặc mật khẩu không đúng"),

  // Validation
  USERNAME_IS_NOT_BLANK(4002, "Tên tài khoản không được để trống"),
  PASSWORD_IS_NOT_BLANK(4003, "Mật khẩu không được để trống"),
  PASSWORD_FORMAT_INVALID(4004, "Mật khẩu phải từ 6 đến 50 ký tự"),

  // Duplicate
  DUPLICATE_USERNAME(4005, "Tên tài khoản đã tồn tại. Vui lòng thử lại."),

  GET_USERS_SUCCESS(2000, "Lấy danh sách người dùng thành công"),
  DELETE_USER_SUCCESS(2001, "Xóa người dùng thành công"),
  ;

  private final int code;
  private final String message;

  CommonMsg(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
