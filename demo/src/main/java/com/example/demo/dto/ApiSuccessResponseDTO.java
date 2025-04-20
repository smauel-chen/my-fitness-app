// src/main/java/com/example/demo/dto/ApiSuccessResponseDTO.java
package com.example.demo.dto;

public class ApiSuccessResponseDTO {
    private int status;
    private String message;

    public ApiSuccessResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
