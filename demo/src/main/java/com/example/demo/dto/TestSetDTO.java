package com.example.demo.dto;

import lombok.Data;

@Data
public class TestSetDTO {
    
    private Long typeId;
    private Integer reps;
    private Integer weight;

    public TestSetDTO() {}

    public TestSetDTO(Long typeId, Integer reps, Integer weight) {
        this.typeId = typeId;
        this.reps = reps;
        this.weight = weight;
    }
}
