package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "訓練模板詳情 DTO")
public class TemplateDetailDTO {

    @Schema(description = "模板 ID", example = "101")
    private Long id;

    @Schema(description = "模板標題", example = "胸部增肌計畫")
    private String title;

    @Schema(description = "預定訓練日期", example = "2025-06-01")
    private LocalDate plannedDate;

    @Schema(description = "包含的動作與訓練組清單")
    private List<ExerciseDetailDTO> exercises;

    public TemplateDetailDTO(Long id, String title, LocalDate plannedDate, List<ExerciseDetailDTO> exercises) {
        this.id = id;
        this.title = title;
        this.plannedDate = plannedDate;
        this.exercises = exercises;
    }
}
