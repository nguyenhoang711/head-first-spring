package com.github.nguyenhoang711.head_first_spring.constant;

import lombok.Getter;

@Getter
public enum CommonMsg {
  // Success
  SUCCESS(0, "Thành công"),

  // Error
  FAILED(-1, "Không thành công"),
  SEND_OTP_FAILED(4008, "Gửi mã OTP không thành công"),
  USERNAME_IS_NOT_CORRECT(4007, "Tên tài khoản không đúng"),
  OTP_NOT_EXIST_OR_EXPIRED(4008, "Mã OTP không tồn tại hoặc đã hết hạn"),

  // Auth
  REGISTER_SUCCESS(1000, "Đăng ký thành công"),
  LOGIN_SUCCESS(1001, "Đăng nhập thành công"),
  LOGIN_FAILED(4001, "Tài khoản hoặc mật khẩu không đúng"),
  LOGOUT_SUCCESS(1002, "Đăng xuất thành công"),
  LOGOUT_FAILED(4002, "Đăng xuất không thành công"),
  CHANGE_PASSWORD_SUCCESS(1003, "Đổi mật khẩu thành công"),
  SEND_OTP_SUCCESS(1004, "Gửi mã OTP thành công"),
  RESET_PASSWORD_SUCCESS(1005, "Đặt lại mật khẩu thành công"),
  OTP_NOT_CORRECT(4009, "Mã OTP không chính xác"),
  VERIFY_OTP_SUCCESS(1006, "Xác thực mã OTP thành công"),

  // Validation
  USERNAME_IS_NOT_BLANK(4002, "Tên tài khoản không được để trống"),
  PASSWORD_IS_NOT_BLANK(4003, "Mật khẩu không được để trống"),
  PASSWORD_FORMAT_INVALID(4004, "Mật khẩu phải từ 6 đến 50 ký tự"),
  INVALID_TOKEN(4005, "Token không hợp lệ"),
  OLD_PASSWORD_IS_NOT_CORRECT(4006, "Mật khẩu cũ không đúng"),
  NEW_PASSWORD_IS_NOT_DIFFERENT(4007, "Mật khẩu mới phải khác mật khẩu cũ"),

  // Duplicate
  DUPLICATE_USERNAME(4005, "Tên tài khoản đã tồn tại. Vui lòng thử lại."),

  GET_USERS_SUCCESS(2000, "Lấy danh sách người dùng thành công"),
  USER_NOT_FOUND(4006, "Người dùng không tồn tại"),
  DELETE_USER_SUCCESS(2001, "Xóa người dùng thành công"),
  ;

  private final int code;
  private final String message;

  CommonMsg(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
