package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TemplateSummaryDTO {   
    @Schema(description = "模板id", example = "101")
    private Long id;
    @Schema(description = "模板標題", example = "胸部增肌計畫")
    private String title;
    @Schema(description = "預定訓練日期", example = "2025-05-23")
    private LocalDate plannedDate;

    @Schema(description = "包含的動作與組數摘要")
    private List<ExerciseSummaryDTO> exercises; // ex: ["啞鈴臥推", sets=2]
    @Schema(description = "此模板中出現頻率最高的主肌群（最多三個）", example = "[\"胸部\", \"三頭肌\"]")
    private List<String> mainTags;                // ex: ["胸部", "三頭肌"]

    public TemplateSummaryDTO(Long id, String title, LocalDate plannedDate, List<ExerciseSummaryDTO> exercises, List<String> mainTags) {
        this.id = id;
        this.title = title;
        this.plannedDate = plannedDate;
        this.exercises = exercises;
        this.mainTags = mainTags;
    }
}
