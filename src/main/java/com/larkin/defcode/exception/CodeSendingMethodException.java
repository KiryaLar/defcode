package com.larkin.defcode.exception;

public class CodeSendingMethodException extends DefcodeException {
    private CodeSendingMethodException(String message) {
        super(ExceptionType.INTERNAL_SERVER_ERROR,message);
    }

    public static void telegram(String message) {
      throw new CodeSendingMethodException(message);
    }

    public static void sms(String message) {
      throw new CodeSendingMethodException(message);
    }

    public static void email(String message) {
      throw new CodeSendingMethodException(message);
    }
}
