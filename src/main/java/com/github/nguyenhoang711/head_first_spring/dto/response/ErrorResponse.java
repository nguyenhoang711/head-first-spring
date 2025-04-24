package com.github.nguyenhoang711.head_first_spring.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private int status;
  private String error;
  private String message;

  public ErrorResponse(int status, String error, String message) {
    this.status = status;
    this.error = error;
    this.message = message;
  }
}
