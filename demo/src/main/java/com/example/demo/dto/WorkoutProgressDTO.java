package com.example.demo.dto;

public class WorkoutProgressDTO {
    private String date;
    private Integer totalWeight;

    public WorkoutProgressDTO(String date, Integer totalWeight) {
        this.date = date;
        this.totalWeight = totalWeight;
    }

    public String getDate() {
        return date;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }
}
