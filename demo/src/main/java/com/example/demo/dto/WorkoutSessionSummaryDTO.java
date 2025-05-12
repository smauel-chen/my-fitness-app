package com.example.demo.dto;

public class WorkoutSessionSummaryDTO {
    private Long sessionId;
    private String date;
    private String note;
    private Integer totalWeight;

    public WorkoutSessionSummaryDTO(){}

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
    public void setSessionId(Long sessionId){this.sessionId = sessionId; }
    public void setDate(String date){this.date = date; }
    public void setNote(String note){this.note = note; }
    public void setTotalWeight(Integer totalWeight){this.totalWeight = totalWeight; }
} 