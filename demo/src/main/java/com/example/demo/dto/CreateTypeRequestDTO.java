package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTypeRequestDTO {
    private String name;
    private String mainTag;
    private List<String> secondaryTags = new ArrayList<>();
} 