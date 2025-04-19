//WorkoutSessionDTO
package com.example.demo.dto;

import java.util.List;

public class WorkoutSessionDTO {
    private Long sessionId;
    private String date;
    private String note;
    private List<WorkoutSetDTO> sets;

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
}
