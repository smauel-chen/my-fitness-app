package com.example.demo.dto;

import lombok.Data;

@Data
public class WorkoutSetEditDTO {
    private Integer reps;
    private Integer weight;

    public WorkoutSetEditDTO() {}

    public WorkoutSetEditDTO(Integer reps, Integer weight) {
        this.reps = reps;
        this.weight = weight;
    }
} 
