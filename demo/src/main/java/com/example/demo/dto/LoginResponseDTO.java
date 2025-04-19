package com.example.demo.dto;

public class LoginResponseDTO {
    private String token;
    private Long userId;

    public LoginResponseDTO(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
}
