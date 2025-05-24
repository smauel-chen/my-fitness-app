package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseSummaryDTO {
    @Schema(description = "動作類型 ID", example = "12")
    private Long typeId;
    @Schema(description = "動作名稱", example = "啞鈴臥推")
    private String typeName;
    @Schema(description = "此動作在模板中的組數", example = "3")
    private int sets;

    public ExerciseSummaryDTO(Long typeId, String typeName, int sets) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.sets = sets;
    }
}
