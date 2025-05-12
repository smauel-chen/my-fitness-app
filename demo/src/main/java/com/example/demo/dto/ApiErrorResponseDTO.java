// src/main/java/com/example/demo/dto/ApiErrorResponseDTO.java
package com.example.demo.dto;

public class ApiErrorResponseDTO {
    private int status;
    private String message;
    private String path;
    private String name;

    public ApiErrorResponseDTO(){}

    public ApiErrorResponseDTO(int status, String message, String path, String name) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.name = name;
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public String getName() { return name; }


    public void setStatus(int status){this.status = status;}
    public void setMessage(String message){this.message = message;}
    public void setPath(String path){this.path = path;}
    public void setName(String name){this.name = name;}
}
