package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// SetDetailDTO.java
@Data
@NoArgsConstructor
public class SetDetailDTO {
    private Integer reps;
    private Integer weight;

    public SetDetailDTO(Integer reps, Integer weight) {
        this.reps = reps;
        this.weight = weight;
    }
}