package com.example.demo.controller;

import java.util.List;

import com.example.demo.dto.CreateTemplateRequestDTO;
import com.example.demo.dto.TemplateDetailDTO;
import com.example.demo.dto.TemplateSummaryDTO;
import com.example.demo.service.TemplateSessionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/{id}")
public class TemplateSessionController {

    private final TemplateSessionService workoutTemplateService;

    public TemplateSessionController(TemplateSessionService workoutTemplateService) {
        this.workoutTemplateService = workoutTemplateService;
    }
    
    @GetMapping("/templates")
    public List<TemplateSummaryDTO> getTemplatesByUser(@PathVariable Long id) {
        return workoutTemplateService.getTemplateSummariesByUserId(id);
    }

    @PostMapping("/template")
    public ResponseEntity<String> createTemplate(
        @PathVariable Long id,
        @RequestBody CreateTemplateRequestDTO dto
    ) {
        workoutTemplateService.createTemplate(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Template created");
    }

    @GetMapping("/template/{templateId}")
    public ResponseEntity<TemplateDetailDTO> getTemplateDetail(@PathVariable Long id, @PathVariable Long templateId) {
        TemplateDetailDTO dto = workoutTemplateService.getTemplateDetail(templateId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/template/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id, @PathVariable Long templateId) {
        workoutTemplateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/template/{templateId}")
    public ResponseEntity<Void> updateTemplate(
        @PathVariable Long id,
        @PathVariable Long templateId,
        @RequestBody CreateTemplateRequestDTO dto
    ) {
        workoutTemplateService.updateTemplate(templateId, dto);
        return ResponseEntity.noContent().build();
    }




}
