package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseDetailDTO {
    private String typeName;
    private String mainTag;
    private List<String> secondaryTags = new ArrayList<>();
    private List<SetDetailDTO> sets;

    public ExerciseDetailDTO(String typeName, String mainTag, List<SetDetailDTO> sets) {
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