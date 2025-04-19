package com.example.demo.dto;

public class WorkoutSessionSummaryDTO {
    private Long sessionId;
    private String date;
    private String note;
    private Integer totalWeight;

    public WorkoutSessionSummaryDTO(Long sessionId, String date, String note, Integer totalWeight) {
        this.sessionId = sessionId;
        this.date = date;
        this.note = note;
        this.totalWeight = totalWeight;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public Integer getTotalWeight() {
        return totalWeight;
    }
} 