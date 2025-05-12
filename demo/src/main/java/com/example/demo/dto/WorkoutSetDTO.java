//WorkoutSetDTO
package com.example.demo.dto;

public class WorkoutSetDTO {
    private Long id;
    private String type;
    private Integer weight;
    private Integer reps;

    public WorkoutSetDTO(){}

    public WorkoutSetDTO(Long id, String type, Integer weight, Integer reps) {
        this.id = id;
        this.type = type;
        this.weight = weight;
        this.reps = reps;
    }

    // Getters
    public Long getId() { return id; }
    public String getType() { return type; }
    public Integer getWeight() { return weight; }
    public Integer getReps() { return reps; }

    public void setId(Long id) {
        this.id = id;
    }
    public void setReps(Integer reps) {
        this.reps = reps;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public void setType(String type){ this.type = type; }
}
