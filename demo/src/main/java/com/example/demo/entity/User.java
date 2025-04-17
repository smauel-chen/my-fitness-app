package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutSession> workoutSessions = new ArrayList<>();

    public User(){}

    public User(String name, Integer age, String password){
        this.name = name;
        this.age = age;
        this.password = password;
    }

    // getter 和 setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public List<WorkoutSession> getWorkoutSessions() {
        return workoutSessions;
    }

    public void setWorkoutSessions(List<WorkoutSession> workoutSessions) {
        this.workoutSessions = workoutSessions;
    }
}
