package com.example.demo.dto;

import java.util.List;

public class WorkoutSessionRequestDTO {
    private String date;
    private String note;
    private List<WorkoutSetRequestDTO> sets;

    public WorkoutSessionRequestDTO() {}

    public WorkoutSessionRequestDTO(String date, String note, List<WorkoutSetRequestDTO> sets) {
        this.date = date;
        this.note = note;
        this.sets = sets;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<WorkoutSetRequestDTO> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSetRequestDTO> sets) {
        this.sets = sets;
    }
} 