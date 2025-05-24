package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "模板中的單一動作設定")
public class CreateExerciseDTO {

    @Schema(description = "動作類型 ID", example = "12")
    private Long typeId;

    @Schema(description = "包含的組數清單")
    private List<CreateSetDTO> sets = new ArrayList<>();
}
