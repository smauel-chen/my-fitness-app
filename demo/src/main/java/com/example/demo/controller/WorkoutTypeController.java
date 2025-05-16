package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.CreateTypeRequestDTO;
import com.example.demo.entity.WorkoutType;
import com.example.demo.service.WorkoutTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/workout-types")
public class WorkoutTypeController {

    private final WorkoutTypeService workoutTypeService;

    public WorkoutTypeController( WorkoutTypeService workoutTypeService){
        this.workoutTypeService = workoutTypeService;
    }

    //muscleType controll section
    @Operation(
    summary = "新增訓練動作類型",
    description = "建立一個新的訓練動作類型，包含動作名稱與對應的肌群名稱。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "動作類型建立成功"),
        @ApiResponse(responseCode = "400", description = "輸入資料格式錯誤")
    })
    @PostMapping("")
    public ResponseEntity<?> createWorkoutType(@RequestBody CreateTypeRequestDTO workoutTypeRequestDTO){
        ObjectMapper mapper = new ObjectMapper();
        try{
            String backToJson = mapper.writeValueAsString(workoutTypeRequestDTO);
            System.out.println(backToJson);
        } catch (Exception e){
            System.out.println("DTO 轉換失敗：" + e.getMessage());
            e.printStackTrace();
        }
        WorkoutType newType = workoutTypeService.createWorkoutType(workoutTypeRequestDTO);
        return ResponseEntity.ok(newType);
    }



    @Operation(
        summary = "取得所有訓練動作類型",
        description = "回傳目前系統中所有訓練動作類型，包含動作名稱與所對應的肌群名稱。"
    )   
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得動作類型清單"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    @GetMapping("")
    public ResponseEntity<?> getAllWorkoutTypes(){
        List<WorkoutTypeDTO> workoutTypeDTOs = workoutTypeService.getAllTypes();
        return ResponseEntity.ok(workoutTypeDTOs);  
    }

}
