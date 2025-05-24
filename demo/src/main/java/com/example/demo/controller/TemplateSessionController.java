package com.example.demo.controller;

import java.util.List;

import com.example.demo.dto.CreateTemplateRequestDTO;
import com.example.demo.dto.TemplateDetailDTO;
import com.example.demo.dto.TemplateSummaryDTO;
import com.example.demo.service.TemplateSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "訓練模板 API", description = "管理使用者通過模板快速建立紀錄的操作")
@RestController
@RequestMapping("/user/{id}")
public class TemplateSessionController {

    private final TemplateSessionService workoutTemplateService;

    public TemplateSessionController(TemplateSessionService workoutTemplateService) {
        this.workoutTemplateService = workoutTemplateService;
    }
    
    @Operation( 
        summary = "獲取使用者所有訓練模板",
        description = "根據使用者 ID，取得其所有訓練模板的摘要資料，包含動作與主要肌群統計")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功取得模板列表"),
        @ApiResponse(
            responseCode = "404",
            description = "沒有找到使用者",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(example = "{\"error\": \"沒有找到使用者\"}"))
        )
    })
    @GetMapping("/templates")
    public List<TemplateSummaryDTO> getTemplatesByUser(
        @Parameter(description = "使用者id", example = "1")
        @PathVariable Long id
    ) {
        return workoutTemplateService.getTemplateSummariesByUserId(id);
    }


    @Operation(
    summary = "建立新的訓練模板",
    description = "根據使用者 ID 建立一份新的訓練模板，包含標題、預定日期、動作與組數"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "建立成功"),
        @ApiResponse(responseCode = "404", description = "找不到使用者或動作類型",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"沒有找到使用者\"}"))
        ),
        @ApiResponse(responseCode = "400", description = "輸入資料格式錯誤",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"缺少訓練組內容\"}"))
        )
    })
    @PostMapping("/template")
    public ResponseEntity<String> createTemplate(
        @Parameter(description = "使用者 ID", example = "1")
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "訓練模板資料",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateTemplateRequestDTO.class))
        )
        @RequestBody CreateTemplateRequestDTO dto
    ) {
        workoutTemplateService.createTemplate(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Template created");
    }

    @Operation(
        summary = "獲取訓練模板詳細資料",
        description = "根據使用者 ID 與模板 ID，獲取該模板的完整資訊，包含所有動作與組數設定"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功取得模板詳情"),
        @ApiResponse(responseCode = "404", description = "找不到訓練模板",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"error\": \"找不到訓練卡片\"}")))
    })
    @GetMapping("/template/{templateId}")
    public ResponseEntity<TemplateDetailDTO> getTemplateDetail(
        @Parameter(description = "使用者 ID", example = "1") @PathVariable Long id, 
        @Parameter(description = "模板 ID", example = "101") @PathVariable Long templateId) {
        TemplateDetailDTO dto = workoutTemplateService.getTemplateDetail(templateId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/template/{templateId}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id, @PathVariable Long templateId) {
        workoutTemplateService.deleteTemplate(templateId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "更新訓練模板",
        description = "根據使用者 ID 與模板 ID，更新該訓練模板的標題、預定日期與所有訓練內容（動作與組數將會被完整覆蓋）"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "更新成功（無內容回應）"),
        @ApiResponse(responseCode = "404", description = "找不到模板或使用者",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"error\": \"找不到該訓練卡片\"}"))
        ),
        @ApiResponse(responseCode = "400", description = "輸入資料錯誤，例如缺少動作、組數不正確",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"error\": \"找不到指定動作\"}"))
        )
    })
    @PutMapping("/template/{templateId}")
    public ResponseEntity<Void> updateTemplate(
        @Parameter(description = "使用者 ID", example = "1") @PathVariable Long id,
        @Parameter(description = "欲更新的模板 ID", example = "101") @PathVariable Long templateId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "更新後的訓練模板內容（會完全覆蓋原內容）",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateTemplateRequestDTO.class))
        )
        @RequestBody CreateTemplateRequestDTO dto
    ) {
        workoutTemplateService.updateTemplate(templateId, dto);
        return ResponseEntity.noContent().build();
    }
    
}
