package com.larkin.defcode.exception;

public class InvalidJwtTokenException extends DefcodeException{
    public InvalidJwtTokenException(String message) {
        super(ExceptionType.INVALID_TOKEN_EXCEPTION, message);
    }
}
