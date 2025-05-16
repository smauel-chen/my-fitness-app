//WorkoutSessionDTO
package com.example.demo.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkoutAllSessionsDTO {
    private Long sessionId;
    private String title;
    private LocalDate date;
    private List<String> mainTags = new ArrayList<>();

    public WorkoutAllSessionsDTO(Long sessionId, String title, LocalDate date,  List<String> mainTags) {
        this.sessionId = sessionId;
        this.title = title;
        this.date = date;
        this.mainTags = mainTags;
    }

}
