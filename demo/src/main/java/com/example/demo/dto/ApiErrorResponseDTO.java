// src/main/java/com/example/demo/dto/ApiErrorResponseDTO.java
package com.example.demo.dto;

public class ApiErrorResponseDTO {
    private int status;
    private String message;
    private String path;

    public ApiErrorResponseDTO(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}
