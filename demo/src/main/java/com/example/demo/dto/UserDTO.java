package com.example.demo.dto;

public class UserDTO {
    private Long id;
    private String name;
    private Integer age;

    public UserDTO(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    // getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getAge() { return age; }
}
