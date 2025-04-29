package com.example.demo.dto;

public class UserUpdatedRequestDTO {
    private String name;
    private Integer age;

    public UserUpdatedRequestDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public Integer getAge() { return age; }
}
