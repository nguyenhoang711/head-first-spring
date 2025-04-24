package com.github.nguyenhoang711.head_first_spring.dto.response;

import com.github.nguyenhoang711.head_first_spring.constant.CommonMsg;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
  private long code;
  private String message;
  private T data;

  public BaseResponse(long code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public static <T> BaseResponse<T> success(long code, String message, T data) {
    return new BaseResponse<>(code, message, data);
  }

  public static <T> BaseResponse<T> success(CommonMsg msg, T data) {
    return new BaseResponse<>(msg.getCode(), msg.getMessage(), data);
  }

  public static <T> BaseResponse<T> error(CommonMsg msg) {
    return new BaseResponse<>(msg.getCode(), msg.getMessage(), null);
  }
}
