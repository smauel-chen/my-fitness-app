package com.example.demo.dto;

public class WeeklySummaryDTO {
    private String week;
    private Integer totalWeight;

    public WeeklySummaryDTO(){}

    public WeeklySummaryDTO(String week, Integer totalWeight) {
        this.week = week;
        this.totalWeight = totalWeight;
    }

    public String getWeek() {
        return week;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }
    public void setWeek(String week){ this.week = week;}
    public void setTotalWeight(Integer totalWeight){ this.totalWeight = totalWeight;}
}
