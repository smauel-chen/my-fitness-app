// src/main/java/com/example/demo/dto/EnrollRequestDTO.java
package com.example.demo.dto;

public class EnrollRequestDTO {
    private String name;
    private String password;
    private Integer age;

    public EnrollRequestDTO() {}

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }
}
