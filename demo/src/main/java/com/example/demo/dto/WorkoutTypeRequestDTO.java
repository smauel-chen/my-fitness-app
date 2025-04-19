package com.example.demo.dto;

public class WorkoutTypeRequestDTO {
    private String name;
    private String muscleGroup;

    public WorkoutTypeRequestDTO() {}

    public WorkoutTypeRequestDTO(String name, String muscleGroup) {
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    public String getName() {
        return name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }
} 