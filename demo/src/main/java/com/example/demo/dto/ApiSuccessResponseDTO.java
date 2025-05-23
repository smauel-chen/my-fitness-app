// src/main/java/com/example/demo/dto/ApiSuccessResponseDTO.java
package com.example.demo.dto;

public class ApiSuccessResponseDTO {
    private int status;
    private String message;

    public ApiSuccessResponseDTO(){}

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

    public void setStatus(int status){this.status = status;}
    public void setMessage(String message){this.message = message;}
}
