package com.example.demo.dto;

public class WeeklyFrequencyDTO {
    private String weekKey;
    private Integer frequency;

    public WeeklyFrequencyDTO(String weekKey, Integer frequency) {
        this.weekKey = weekKey;
        this.frequency = frequency;
    }

    public String getWeekKey() {
        return weekKey;
    }

    public Integer getFrequency() {
        return frequency;
    }
} 
