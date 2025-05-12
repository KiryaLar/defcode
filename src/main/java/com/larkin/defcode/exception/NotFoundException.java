package com.larkin.defcode.exception;

public class NotFoundException extends DefcodeException {
    private NotFoundException(String message) {
        super(ExceptionType.OBJECT_NOT_FOUND, message);
    }

    public static void admin(String login) {
        throw new NotFoundException("Admin " + login + " not found");
    }

    public static void user(String login) {
        throw new NotFoundException("User " + login + " not found");
    }

    public static void otpConfig() {
        throw new NotFoundException("OTP config not found");
    }

    public static void operation(Integer operationTypeId) {
        throw new NotFoundException("Operation " + operationTypeId + " doesn't exist");
    }
}
