package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseSummaryDTO {
    private Long typeId;
    private String typeName;
    private int sets;

    public ExerciseSummaryDTO(Long typeId, String typeName, int sets) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.sets = sets;
    }
}
