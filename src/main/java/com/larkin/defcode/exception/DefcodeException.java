package com.larkin.defcode.exception;

import lombok.Getter;

public class DefcodeException extends RuntimeException {

    @Getter
    private final ExceptionType exceptionType;

    public DefcodeException(ExceptionType exceptionType ,String message) {
        super(message);
        this.exceptionType = exceptionType;
    }
}
