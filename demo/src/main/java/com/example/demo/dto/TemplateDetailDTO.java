package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateDetailDTO {
    private Long id;
    private String title;
    private LocalDate plannedDate;
    private List<ExerciseDetailDTO> exercises;

    public TemplateDetailDTO(Long id, String title, LocalDate plannedDate, List<ExerciseDetailDTO> exercises) {
        this.id = id;
        this.title = title;
        this.plannedDate = plannedDate;
        this.exercises = exercises;
    }
}
