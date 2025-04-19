package com.example.demo.exception;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private Integer status;
    private String message;
    private LocalDateTime timestamp;

    public ApiErrorResponse(Integer status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters
    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 