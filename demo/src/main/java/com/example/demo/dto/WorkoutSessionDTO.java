//WorkoutSessionDTO
package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class WorkoutSessionDTO {
    private Long sessionId;
    private String date;
    private String note;
    private List<WorkoutSetDTO> sets;

    public WorkoutSessionDTO(){
        this.sets = new ArrayList<>();
    }

    public WorkoutSessionDTO(Long sessionId, String date, String note, List<WorkoutSetDTO> sets) {
        this.sessionId = sessionId;
        this.date = date;
        this.note = note;
        this.sets = sets;
    }

    // Getters
    public Long getSessionId() { return sessionId; }
    public String getDate() { return date; }
    public String getNote() { return note; }
    public List<WorkoutSetDTO> getSets() { return sets; }
    public void setNote(String note) {
        this.note = note;
    }
    public void setSets(List<WorkoutSetDTO> sets) {
        this.sets = sets;
    }
    public void setSessionId(Long sessionId){ this.sessionId = sessionId; }
    public void setDate(String date){ this.date = date; }
}
