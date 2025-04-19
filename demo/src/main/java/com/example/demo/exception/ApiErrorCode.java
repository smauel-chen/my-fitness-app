package com.example.demo.exception;

public enum ApiErrorCode {
    USER_NOT_FOUND,
    SESSION_NOT_FOUND,
    SET_NOT_FOUND,
    TYPE_NOT_FOUND,
    INVALID_CREDENTIALS,
    DUPLICATE_USERNAME,
    MISSING_FIELD,
    UNAUTHORIZED_ACCESS,
    INTERNAL_ERROR,
    SESSION_FOR_USER_NOT_FOUND // catch error findbyidanduserid
}
