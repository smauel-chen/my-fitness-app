package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class WorkoutType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String muscleGroup;

    public WorkoutType(){}

    public WorkoutType(String name, String muscleGroup){
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setMuscleGroup(String muscleGroup){
        this.muscleGroup = muscleGroup;
    }

    public String getMuscleGroup(){
        return muscleGroup;
    }
}
