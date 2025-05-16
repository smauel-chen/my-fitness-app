package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExerciseSummaryDTO {
    private String typeName;
    private int sets;

    public ExerciseSummaryDTO(String typeName, int sets) {
        this.typeName = typeName;
        this.sets = sets;
    }
}
