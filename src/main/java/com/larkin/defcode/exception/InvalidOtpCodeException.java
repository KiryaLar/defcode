package com.larkin.defcode.exception;

public class InvalidOtpCodeException extends DefcodeException {
    public InvalidOtpCodeException() {
        super(ExceptionType.VALIDATION_EXCEPTION, "Invalid otp code");
    }
}
