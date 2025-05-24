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
@Schema(description = "模板中的單一訓練動作")
public class ExerciseDetailDTO {

    @Schema(description = "動作類型 ID", example = "12")
    private Long typeId;

    @Schema(description = "動作名稱", example = "槓鈴臥推")
    private String typeName;

    @Schema(description = "主要訓練肌群", example = "胸部")
    private String mainTag;

    @Schema(description = "次要訓練肌群", example = "[\"三頭肌\", \"前三角\"]")
    private List<String> secondaryTags = new ArrayList<>();

    @Schema(description = "包含的訓練組清單")
    private List<SetDetailDTO> sets;

    public ExerciseDetailDTO(Long typeId, String typeName, String mainTag, List<SetDetailDTO> sets) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.mainTag = mainTag;
        this.sets = sets;
    }

    public ExerciseDetailDTO(String typeName, String mainTag, List<String> secondaryTags, List<SetDetailDTO> sets) {
        this.typeName = typeName;
        this.mainTag = mainTag;
        this.secondaryTags = secondaryTags;
        this.sets = sets;
    }
}