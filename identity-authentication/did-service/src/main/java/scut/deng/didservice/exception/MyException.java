package scut.deng.didservice.exception;

import scut.deng.didservice.pojo.constant.ErrorCode;

public class MyException extends Exception {
    private final ErrorCode errorCode;
    private String message;

    public MyException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public MyException(String message) {
        this.errorCode = ErrorCode.SYSTEM_ERROR;
        this.message = message;
    }
    public MyException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder("MyException: [")
                .append(this.errorCode).append("], Message: [")
                .append(this.message).append("]");

        return sb.toString();
    }
}
