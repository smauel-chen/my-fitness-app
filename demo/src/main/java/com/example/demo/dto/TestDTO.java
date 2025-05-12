package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TestDTO {
    String date;
    String note;
    List<TestSetDTO> sets = new ArrayList<>();

    public TestDTO(){}

    public TestDTO(String date, String note, List<TestSetDTO> sets){
        this.date = date;
        this.note = note;
        this.sets = sets;
    }
}
