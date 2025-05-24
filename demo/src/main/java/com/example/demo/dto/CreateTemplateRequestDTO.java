package com.example.demo.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立訓練模板請求 DTO")
public class CreateTemplateRequestDTO {
    @Schema(description = "模板標題", example = "胸部增肌計畫")
    private String title;

    @Schema(description = "預定訓練日期", example = "2025-06-01")
    private LocalDate plannedDate;

    @Schema(description = "包含的動作清單")
    private List<CreateExerciseDTO> exercises = new ArrayList<>();
}

