package com.example.demo.dto;

public class MuscleGroupTotalDTO {
    private String muscleGroup;
    private Integer totalWeight;

    public MuscleGroupTotalDTO(String muscleGroup, Integer totalWeight) {
        this.muscleGroup = muscleGroup;
        this.totalWeight = totalWeight;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }
}
