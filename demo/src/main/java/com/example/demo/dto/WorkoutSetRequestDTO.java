package com.example.demo.dto;

import lombok.Data;

@Data
public class WorkoutSetRequestDTO {
    private Long typeId;
    private Integer reps;
    private Integer weight;

    public WorkoutSetRequestDTO() {}

    public WorkoutSetRequestDTO(Long typeId, Integer reps, Integer weight) {
        this.typeId = typeId;
        this.reps = reps;
        this.weight = weight;
    }
    
    public Long getTypeId() {return typeId;}
    public void setTypeId(Long typeId) {this.typeId = typeId;}
    
    public Integer getReps() {return reps;}
    public void setReps(Integer reps) {this.reps = reps;}
    
    public Integer getWeight() {return weight;}
    public void setWeight(Integer weight) {this.weight = weight;}
    
} 
