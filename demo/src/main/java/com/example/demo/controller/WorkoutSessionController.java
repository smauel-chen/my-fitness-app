package com.example.demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;
import com.example.demo.dto.WeeklyFrequencyDTO;
import com.example.demo.dto.WeeklySummaryDTO;
import com.example.demo.dto.WorkoutProgressDTO;
import com.example.demo.dto.WorkoutSessionRequestDTO;
import com.example.demo.dto.WorkoutSessionSummaryDTO;
import com.example.demo.service.WorkoutSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("user/{id}")
public class WorkoutSessionController {

    private final WorkoutSessionService workoutSessionService;
    
    public WorkoutSessionController( WorkoutSessionService workoutSessionService ) {
        this.workoutSessionService = workoutSessionService;
    }

    @Operation(
    summary = "新增訓練課表",
    description = "為指定的使用者新增一筆訓練課表，內容包含訓練日期、備註與多筆訓練組合"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功建立訓練課表"),
        @ApiResponse(responseCode = "404", description = "找不到使用者 ID"),
    })
    @Parameter(name = "id", description = "使用者 ID", required = true)    
    @PostMapping("/session")
    public ResponseEntity<?> addWorkoutSession(
            @PathVariable Long id,
            @RequestBody WorkoutSessionRequestDTO workoutSessionRequestDTO
    ){
        workoutSessionService.addSession(id, workoutSessionRequestDTO);
        return ResponseEntity.ok("Create session for user: " + id);
    }

    @Operation(summary = "取得使用者所有訓練紀錄", description = "依照使用者 ID 回傳所有訓練課表與細節")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/sessions")
    public ResponseEntity<?> getWorkoutSessions(@Parameter(description = "使用者ID", example = "1") @PathVariable Long id){
        return ResponseEntity.ok(workoutSessionService.getAllSession(id));
    }

    @Operation(summary = "刪除使用者的課表", description = "根據 課表ID 刪除課表")
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者或是找不到課表")
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<?> deleteWorkoutSession(
        @PathVariable Long id, @PathVariable Long sessionId
    ){
        workoutSessionService.deleteSession(id, sessionId);
        return ResponseEntity.ok("removed session:" + sessionId);
    }
    
    @Operation(summary = "取得使用者不同日期訓練課表的資訊", description = "依照使用者 ID 回傳不同日期訓練課表的內容和總重量")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/sessions/summary")
    public ResponseEntity<?> getWorkoutSessionSummaryDTO(@PathVariable Long id) {
        List<WorkoutSessionSummaryDTO> summaryDTOs = workoutSessionService.getWorkoutSessionSummary(id);
        return ResponseEntity.ok(summaryDTOs);
    }

    @Operation(summary = "取得使用者每週的訓練紀錄整理", description = "依照使用者 ID 回傳每週的訓練課表整理")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/sessions/weekly-summary")
    public ResponseEntity<?> getWeeklySummary( @PathVariable Long id ){  
        List<WeeklySummaryDTO> weeklySummaryDTOs = workoutSessionService.getWeeklySummary(id);
        return ResponseEntity.ok(weeklySummaryDTOs);
    }


    @Operation(
        summary = "取得每週訓練頻率統計",
        description = "根據指定使用者 ID，回傳該使用者各週訓練的天數。每週的訓練日只會計算一次，適合用於顯示訓練習慣與規律程度的圖表。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得訓練頻率資料"),
        @ApiResponse(responseCode = "404", description = "找不到指定使用者"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    @GetMapping("/sessions/weekly-frequency")
    public ResponseEntity<?> getWeeklyWorkoutDays(@PathVariable Long id){
        List<WeeklyFrequencyDTO> weeklyFrequencyDTOs = workoutSessionService.getWeeklyFrequency(id);
        return ResponseEntity.ok(weeklyFrequencyDTOs);
    }

    
    @Operation(
        summary = "查詢訓練動作進度",
        description = "根據使用者 ID 與動作類型 ID，取得該動作在各次訓練中的總重量變化情形。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得訓練進度"),
        @ApiResponse(responseCode = "404", description = "找不到使用者")
    })
    @Parameters({
        @Parameter(name = "id", description = "使用者 ID", required = true),
        @Parameter(name = "typeId", description = "動作類型 ID", required = true)
    })
    @GetMapping("/muscle-groups/total-weight")
    public ResponseEntity<?> getTotalWeightsByMuscleGroup(@PathVariable Long id){
        List<MuscleGroupTotalDTO> muscleGroupTotalDTOs = workoutSessionService.getTotalWeightsByMuscleGroup(id);
        return ResponseEntity.ok(muscleGroupTotalDTOs);
    }


    @Operation(
        summary = "取得熱門訓練動作",
        description = "根據使用者 ID 統計其所有訓練中最常使用的動作類型（依照出現次數排序）"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得熱門訓練動作資料"),
        @ApiResponse(responseCode = "404", description = "找不到該使用者")
    })
    @Parameter(name = "id", description = "使用者 ID", required = true)
    @GetMapping("/workouts/popular-types")
    public ResponseEntity<?> getPopularWorkoutTypes(@PathVariable Long id){
        List<PopularTypesDTO> popularTypesDTOs = workoutSessionService.getPopularWorkoutType(id);
        return ResponseEntity.ok(popularTypesDTOs);
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

    //stream version
    @Operation(
    summary = "取得訓練進度紀錄",
    description = "根據指定使用者 ID 與動作類型 ID，回傳該使用者每一次訓練該動作時的總重量，用於前端繪製訓練成效折線圖。"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得進度資料"),
        @ApiResponse(responseCode = "404", description = "找不到指定使用者"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    @GetMapping("/workouts/progress")
    public ResponseEntity<?> getWorkoutProgressByType(
        @PathVariable Long id, @RequestParam Long typeId
    ){
        List<WorkoutProgressDTO> progressDTOs = workoutSessionService.getWorkoutProgressByType(id, typeId);
        return ResponseEntity.ok(progressDTOs);
    }
}
