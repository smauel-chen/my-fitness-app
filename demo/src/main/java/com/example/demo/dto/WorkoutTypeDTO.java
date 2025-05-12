package com.example.demo.dto;

public class WorkoutTypeDTO {
    private Long id;
    private String name;
    private String muscleGroup;

    public WorkoutTypeDTO(){}

    public WorkoutTypeDTO(Long id, String name, String muscleGroup) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setId(Long id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setMuscleGroup(String muscleGroup){ this.muscleGroup = muscleGroup; } 
}
