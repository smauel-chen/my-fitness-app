package com.example.demo.dto;

public class WeeklyFrequencyDTO {
    private String weekKey;
    private Integer frequency;

    public WeeklyFrequencyDTO(){}

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
    public void setWeekKey(String weekKey){ this.weekKey = weekKey;}
    public void setFrequency(Integer frequency){ this.frequency = frequency;}
} 
