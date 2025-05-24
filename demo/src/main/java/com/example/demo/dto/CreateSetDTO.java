package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "每組的次數與重量設定")
public class CreateSetDTO {

    @Schema(description = "次數", example = "8")
    private Integer reps;

    @Schema(description = "重量", example = "50")
    private Integer weight;
}
