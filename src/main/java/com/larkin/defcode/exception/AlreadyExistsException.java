package com.larkin.defcode.exception;

public class AlreadyExistsException extends DefcodeException {
    private AlreadyExistsException(String message) {
        super(ExceptionType.BAD_REQUEST_EXCEPTION, message);
    }

    public static AlreadyExistsException admin() {
        throw new AlreadyExistsException("Admin already exists");
    }

    public static AlreadyExistsException user(String login) {
        throw new AlreadyExistsException("User with " + login + " already exists");
    }
}
