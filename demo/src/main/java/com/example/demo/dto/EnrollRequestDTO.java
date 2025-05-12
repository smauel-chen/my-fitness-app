// src/main/java/com/example/demo/dto/EnrollRequestDTO.java
package com.example.demo.dto;

public class EnrollRequestDTO {
    private String name;
    private String password;
    private Integer age;

    public EnrollRequestDTO(){}

    public EnrollRequestDTO(String name, String password, Integer age) {
        this.name = name;
        this.password = password;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }
    public void setName(String name){this.name = name;}
    public void setPassword(String password){this.password = password;}
    public void setAge(Integer age){this.age = age;}

}
