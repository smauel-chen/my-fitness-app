package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

// SetDetailDTO.java
@Data
@NoArgsConstructor
@Schema(description = "單一訓練組的次數與重量")
public class SetDetailDTO {

    @Schema(description = "次數", example = "8")
    private Integer reps;

    @Schema(description = "重量", example = "50")
    private Integer weight;

    public SetDetailDTO(Integer reps, Integer weight) {
        this.reps = reps;
        this.weight = weight;
    }
}