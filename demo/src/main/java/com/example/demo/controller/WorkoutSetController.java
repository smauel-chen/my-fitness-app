package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WorkoutSetEditDTO;
import com.example.demo.dto.WorkoutSetRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.example.demo.service.WorkoutSetService;

@RequestMapping("/user/{id}/session/{sessionId}/set")
@RestController
public class WorkoutSetController {
    
    private final WorkoutSetService workoutSetService;

    public WorkoutSetController(WorkoutSetService workoutSetService){
        this.workoutSetService = workoutSetService;
    }

    @Operation(
        summary = "新增訓練組合",
        description = "為指定使用者的指定課表新增一筆訓練組，包含動作類型、重量與次數資訊"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功新增訓練組合"),
        @ApiResponse(responseCode = "404", description = "找不到使用者或課表"),
    })
    @Parameters({
        @Parameter(name = "id", description = "使用者 ID", required = true),
        @Parameter(name = "sessionID", description = "課表 ID", required = true)
    })
    @PostMapping("")
    public ResponseEntity<?> addWorkoutSetToSession(
            @PathVariable Long id, @PathVariable Long sessionId,
            @RequestBody WorkoutSetRequestDTO workoutSetRequestDTO
    ){
        workoutSetService.addSet(id, sessionId, workoutSetRequestDTO);
        return ResponseEntity.ok("Create a new set successed");
    }   
    @Operation(
        summary = "刪除訓練組",
        description = "刪除指定使用者的某一課表中的某一筆訓練組"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功刪除訓練組"),
        @ApiResponse(responseCode = "404", description = "找不到對應的使用者、課表或訓練組")
    })
    @Parameters({
        @Parameter(name = "userId", description = "使用者 ID", required = true),
        @Parameter(name = "sessionId", description = "課表 ID", required = true),
        @Parameter(name = "setId", description = "訓練組 ID", required = true)
    })
    @DeleteMapping("/{setId}")
    public ResponseEntity<?> deleteWorkoutSet(
        @PathVariable Long id,
        @PathVariable Long sessionId,
        @PathVariable Long setId
    ) {
        workoutSetService.deleteSet(id, sessionId, setId);
        return ResponseEntity.ok("Delete successed");
    }
    

    @Operation(
        summary = "更新訓練組資訊",
        description = "根據使用者 ID、課表 ID，更新指定訓練組的重量、次數"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功更新訓練組"),
        @ApiResponse(responseCode = "404", description = "找不到指定的使用者、課表或訓練組")
    })
    @Parameters({
        @Parameter(name = "id", description = "使用者 ID", required = true),
        @Parameter(name = "sessionId", description = "課表 ID", required = true),
        @Parameter(name = "setId", description = "訓練組 ID", required = true)
    })    
    @PutMapping("/{setId}")
    public ResponseEntity<?> updateWorkoutSet(
        @PathVariable Long id,
        @PathVariable Long sessionId, 
        @PathVariable Long setId,
        @RequestBody WorkoutSetEditDTO newSetDTO
    ){
        workoutSetService.updateSet(id, sessionId, setId, newSetDTO);
        return ResponseEntity.ok("Update set successed");
    }   
}
