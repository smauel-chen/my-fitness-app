package com.example.demo.dto;

public class LoginRequestDTO {
    private String name;
    private String password;

    public LoginRequestDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }
    public String getPassword() { return password; }
} 