package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.SubmitWeightDTO;
import com.example.demo.dto.WeightTrendDTO;
import com.example.demo.service.BodyWeightRecordService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/user/{id}")
public class BodyWeightRecordController {
    
    private BodyWeightRecordService bodyWeightRecordService;

    BodyWeightRecordController(BodyWeightRecordService bodyWeightRecordService){
        this.bodyWeightRecordService = bodyWeightRecordService;
    }

    //chart 4
    @GetMapping("/charts/weight-trend")
    public List<WeightTrendDTO> getWeightTrend(
        @PathVariable Long id,
        @RequestParam String period
    ) {
        return bodyWeightRecordService.getWeightTrend(id, period);
    }

    @PostMapping("/weight")
    public ResponseEntity<Void> submitTodayWeight(
        @PathVariable Long id,
        @RequestBody SubmitWeightDTO dto
    ) {
        bodyWeightRecordService.saveOrUpdateWeight(id, dto);
        return ResponseEntity.ok().build();
    }

}
