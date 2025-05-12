package com.larkin.defcode.exception;


public class IllegalDurationFormatException extends DefcodeException {

    public IllegalDurationFormatException() {
        super(ExceptionType.BAD_REQUEST_EXCEPTION, "Illegal duration format");
    }
}
