package com.example.demo.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequestDTO {
        private String title;
    private LocalDate date;
    private List<CreateExerciseDTO> exercises = new ArrayList<>();
} 