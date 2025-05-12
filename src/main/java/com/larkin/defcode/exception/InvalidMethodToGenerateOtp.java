package com.larkin.defcode.exception;

public class InvalidMethodToGenerateOtp extends DefcodeException {
    public InvalidMethodToGenerateOtp(String method) {
        super(ExceptionType.BAD_REQUEST_EXCEPTION, "Invalid sending method:" + method);
    }
}
