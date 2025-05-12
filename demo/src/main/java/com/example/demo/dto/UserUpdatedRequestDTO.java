package com.example.demo.dto;

public class UserUpdatedRequestDTO {
    private String name;
    private Integer age;

    public UserUpdatedRequestDTO(){}

    public UserUpdatedRequestDTO(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public Integer getAge() { return age; }
    public void setName(String name){this.name = name;}
    public void setAge(Integer age){this.age = age;}
}
