package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.dto.SessionDetailDTO;
import com.example.demo.dto.VolumeProgressDTO;
import com.example.demo.dto.CreateSessionRequestDTO;
import com.example.demo.service.WorkoutSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RequestMapping("/user/{id}")
@RestController
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;
    
    public WorkoutSessionController( WorkoutSessionService workoutSessionService ) {
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(
    summary = "新增訓練課表",
    description = "為指定的使用者新增一筆訓練課表，內容包含訓練title、與多筆訓練組合"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功建立訓練課表"),
        @ApiResponse(responseCode = "404", description = "找不到使用者 ID"),
    })
    @Parameter(name = "id", description = "使用者 ID", required = true)   
    @PostMapping("/session")
    public ResponseEntity<?> addWorkoutSession(@RequestBody CreateSessionRequestDTO requestDTO, @PathVariable Long id) {
        workoutSessionService.addSession(id, requestDTO);
        return ResponseEntity.ok(requestDTO);
    }

    @Operation(summary = "取得使用者所有訓練紀錄", description = "依照使用者 ID 回傳所有訓練課表與細節")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/sessions")
    public ResponseEntity<?> getWorkoutSessions(@Parameter(description = "使用者ID", example = "1") @PathVariable Long id){
        return ResponseEntity.ok(workoutSessionService.getAllSession(id));
    }


    @Operation(summary = "刪除使用者的課表", description = "根據 課表ID 刪除課表")
    @Parameters({
        @Parameter(name = "id", description = "使用者 ID", required = true),
        @Parameter(name = "sessionId", description = "訓練編號", required = true)})  
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者或是找不到課表")
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> deleteWorkoutSession(
        @PathVariable Long id, @PathVariable Long sessionId
    ){
        workoutSessionService.deleteSession(id, sessionId);
        return ResponseEntity.ok("removed session:" + sessionId);
    }

    @PutMapping("/session/{sessionId}")
    public ResponseEntity<CreateSessionRequestDTO> updateSession(
        @PathVariable Long id,
        @PathVariable Long sessionId,
        @RequestBody CreateSessionRequestDTO dto
    ) {
        workoutSessionService.updateSession(id, sessionId, dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<SessionDetailDTO> getSessionById(@PathVariable Long id, @PathVariable Long sessionId) {
        SessionDetailDTO result = workoutSessionService.getSessionDetailById(id, sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/session-ids")
    public ResponseEntity<List<Long>> getSessionIds(@Parameter(description = "使用者ID", example = "1")@PathVariable Long id) {
        return ResponseEntity.ok(workoutSessionService.getAllSessionIds(id));
    }

    //通過卡片新增訓練紀錄
    @PostMapping("/session/from-template/{templateId}")
    public ResponseEntity<String> createSessionFromTemplate(
        @PathVariable Long id,
        @PathVariable Long templateId
    ) {
        workoutSessionService.createFromTemplate(id, templateId);
        return ResponseEntity.status(HttpStatus.CREATED).body("建立成功");
    }

    //chart 1
    @GetMapping("/charts/tag-frequency")
    public Map<String, Long> getWorkoutTagFrequency(
        @PathVariable Long id,
        @RequestParam String period // e.g., "weekly", "monthly", etc.
    ) {
        return workoutSessionService.getTagFrequency(id, period);
    }

    //chart 2
    @GetMapping("/charts/muscle-balance")
    public Map<String, Long> getMuscleGroupBalance(
        @PathVariable Long id,
        @RequestParam String period
    ) {
        return workoutSessionService.getMuscleGroupBalance(id, period);
    }

    //chart 3
    @GetMapping("/charts/volume-progress")
    public List<VolumeProgressDTO> getVolumeProgress(
        @PathVariable Long id,
        @RequestParam Long typeId,
        @RequestParam int count
    ) {
        return workoutSessionService.getVolumeProgress(id, typeId, count);
    }
    
    // @GetMapping("/user/{id}/workouts/progress")
    // public ResponseEntity<?> getWorkoutProgressByType(
    //     @PathVariable Long id, @RequestParam Long TypeId
    // ){
    //     return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
    //         List<WorkoutProgressDTO> progressDTOs = new ArrayList<>();
    //         for(WorkoutSession session:workoutSessionRepository.findByUser_Id(user.getId())){
    //             Integer toptalWeight = workoutSetRepository.findBySessionId(session.getId()).stream()
    //             .filter(set -> set.getTypeId().equals(TypeId))
    //             .mapToInt(set -> set.getReps() * set.getWeight())
    //             .sum();
    //             if(toptalWeight > 0){
    //                 progressDTOs.add(new WorkoutProgressDTO(session.getDate(),toptalWeight));
    //             }
    //         }
    //         return ResponseEntity.ok(progressDTOs); 
    //     }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    // }

}
