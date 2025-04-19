//WorkoutSetDTO
package com.example.demo.dto;

public class WorkoutSetDTO {
    private Long id;
    private String type;
    private Integer weight;
    private Integer reps;

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
}
