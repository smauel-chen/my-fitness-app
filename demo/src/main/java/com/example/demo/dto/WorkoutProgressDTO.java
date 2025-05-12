package com.example.demo.dto;

public class WorkoutProgressDTO {
    private String date;
    private Integer totalWeight;

    public WorkoutProgressDTO(){}

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
    public void setDate(String date){ this.date = date; }
    public void setTotalWeight(Integer totalWeight){ this.totalWeight = totalWeight;}
}
