package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SessionDetailDTO {
    private Long id;
    private String title;
    private LocalDate date;
    private List<ExerciseDetailDTO> exercises;

    public SessionDetailDTO(Long id, String title, LocalDate date, List<ExerciseDetailDTO> exercises) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.exercises = exercises;
    }
}

