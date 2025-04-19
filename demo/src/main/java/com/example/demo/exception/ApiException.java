package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final ApiErrorCode errorCode;
    private final HttpStatus httpStatus;

    public ApiException(ApiErrorCode errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
