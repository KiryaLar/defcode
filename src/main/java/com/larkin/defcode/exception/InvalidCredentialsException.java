package com.larkin.defcode.exception;

public class InvalidCredentialsException extends DefcodeException {
    public InvalidCredentialsException(String message) {
        super(ExceptionType.UNAUTHORIZED_EXCEPTION, message);
    }
}
