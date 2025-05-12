package com.larkin.defcode.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionType {
  OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
  UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED.value()),
  FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN.value()),
  BAD_REQUEST_EXCEPTION(HttpStatus.BAD_REQUEST.value()),
  VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST.value()),
  INVALID_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST.value()),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value()),;

  private final int statusCode;


  ExceptionType(int statusCode) {
    this.statusCode = statusCode;
  }
}
