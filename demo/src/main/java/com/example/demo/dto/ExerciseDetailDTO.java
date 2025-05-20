package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDetailDTO {
    private Long typeId;
    private String typeName;
    private String mainTag;
    private List<String> secondaryTags = new ArrayList<>();
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