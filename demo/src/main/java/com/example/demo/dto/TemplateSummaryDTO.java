package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TemplateSummaryDTO {   
    private Long id;
    private String title;
    private LocalDate plannedDate;

    private List<ExerciseSummaryDTO> exercises; // ex: ["啞鈴臥推", sets=2]
    private List<String> mainTags;                // ex: ["胸部", "三頭肌"]

    public TemplateSummaryDTO(Long id, String title, LocalDate plannedDate, List<ExerciseSummaryDTO> exercises, List<String> mainTags) {
        this.id = id;
        this.title = title;
        this.plannedDate = plannedDate;
        this.exercises = exercises;
        this.mainTags = mainTags;
    }
}
