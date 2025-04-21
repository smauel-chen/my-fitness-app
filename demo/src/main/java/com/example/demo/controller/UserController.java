package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

//config


//entity
import com.example.demo.entity.*;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
//repository
import com.example.demo.repository.*;



//security
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.demo.config.JwtUtil;
import com.example.demo.dto.ApiSuccessResponseDTO;
import com.example.demo.dto.EnrollRequestDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;


//dto
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdatedRequestDTO;
import com.example.demo.dto.WeeklyFrequencyDTO;
import com.example.demo.dto.WeeklySummaryDTO;
import com.example.demo.dto.WorkoutProgressDTO;
import com.example.demo.dto.WorkoutSessionDTO;
import com.example.demo.dto.WorkoutSessionRequestDTO;
import com.example.demo.dto.WorkoutSessionSummaryDTO;
import com.example.demo.dto.WorkoutSetDTO;
import com.example.demo.dto.WorkoutSetRequestDTO;
import com.example.demo.dto.WorkoutTypeDTO;
import com.example.demo.dto.WorkoutTypeRequestDTO;


//swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;





@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutTypeRepository workoutTypeRepository;



    //DataBase
    private static final Map<String, String> typeToMuscleGroup = Map.of(
        "臥推","胸",
        "肩推","肩",
        "深蹲","腿",
        "硬舉","腿",
        "引體向上","背",
        "坐姿划船","背",
        "坐姿下拉","背"        
    );

    UserController( UserRepository userRepository,
                    WorkoutSessionRepository workoutSessionRepository,
                    WorkoutSetRepository workoutSetRepository,
                    WorkoutTypeRepository workoutTypeRepository,
                    AuthenticationManager authenticationManager,
                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutTypeRepository = workoutTypeRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    private String findWorkoutTypeNameByTypeId(Long TypeId){
        //WorkoutType::getName ---> WorkoutType -> WorkoutType.getName()
        //return workoutTypeRepository.findById(TypeId).map(WorkoutType::getName).orElseGet(()->"Invalid id");
        //orElseGet(lambda parameter) orElse(other parameter)//前者不會先載入，後者不管如何都會先載入
        return workoutTypeRepository.findById(TypeId).map(WorkoutType::getName).orElseThrow(() ->
            new ApiException(ApiErrorCode.TYPE_NOT_FOUND,"沒有對應的部位, typeID:" + TypeId, HttpStatus.NOT_FOUND)
        );
    }

    private Integer calculateTotalWeight(List<WorkoutSet> sets) {
        return sets.stream()
                   .mapToInt(set -> set.getWeight() * set.getReps())
                   .sum();
    }

    @Operation(summary = "註冊新使用者", description = "傳入使用者資料（名稱、密碼、年齡）來建立新帳號")
    @ApiResponse(responseCode = "201", description = "註冊成功")
    @ApiResponse(responseCode = "400", description = "資料格式錯誤")
    @PostMapping("/user")
    public ResponseEntity<ApiSuccessResponseDTO> enrollUser(@RequestBody EnrollRequestDTO enrollRequestDTO) {
        // 檢查是否已有相同名稱的使用者
        if (userRepository.findByName(enrollRequestDTO.getName()).isPresent()) {
            throw new ApiException(ApiErrorCode.USER_ALREADY_EXISTS, "使用者名稱已存在", HttpStatus.BAD_REQUEST);
        }

        // 加密密碼
        String encryptedPassword = passwordEncoder.encode(enrollRequestDTO.getPassword());

        User user = new User(enrollRequestDTO.getName(), enrollRequestDTO.getAge(), encryptedPassword);
        userRepository.save(user);

        ApiSuccessResponseDTO response = new ApiSuccessResponseDTO(201, "註冊成功！");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    

    //PUT update
    @Operation(summary = "更新使用者資料", description = "更新使用者名稱與年齡")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")    
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
        @Parameter(description = "要更新的使用者ID", example = "1") @PathVariable Long id, @RequestBody UserUpdatedRequestDTO userUpdatedRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        user.setName(userUpdatedRequestDTO.getName());
        user.setAge(userUpdatedRequestDTO.getAge());
        userRepository.save(user);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getName(), user.getAge()));
    }

    @Operation(summary = "取得所有使用者", description = "回傳系統中所有使用者的基本資料清單（不含密碼）")
    @ApiResponse(responseCode = "200", description = "成功取得使用者清單")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userRepository.findAll().stream()
            .map(user -> new UserDTO(user.getId(), user.getName(), user.getAge()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }    
    @Operation(summary = "刪除使用者", description = "根據 ID 刪除使用者")
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@Parameter(description = "要刪除的使用者ID", example = "1")@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        userRepository.delete(user);
        return ResponseEntity.ok("Deleted user: " + user.getName());
    }

    @Operation(summary = "取得使用者資訊", description = "根據 ID 回傳使用者名稱與年齡")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")    
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(
        @Parameter(description = "要獲取的使用者ID", example = "1")    @PathVariable Long id
    ) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(
                ApiErrorCode.USER_NOT_FOUND,
                "使用者不存在 ID：" + id,
                HttpStatus.NOT_FOUND
            )
        );
        UserDTO dto = new UserDTO(user.getId(), user.getName(), user.getAge());
        return ResponseEntity.ok(dto);
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
    @PostMapping("/user/{id}/session")
    public ResponseEntity<?> addWorkoutSession(
            @PathVariable Long id,
            @RequestBody WorkoutSessionRequestDTO workoutSessionRequestDTO
    ){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        
        // 1. 建立 WorkoutSession 物件，並設定基本欄位與 user
        WorkoutSession session = new WorkoutSession();
        session.setDate(workoutSessionRequestDTO.getDate());
        session.setNote(workoutSessionRequestDTO.getNote());
        session.setUser(user);

        // 2. 建立每一組 set 的實體物件並與 session 關聯
        List<WorkoutSet> sets = workoutSessionRequestDTO.getSets().stream()
            .map(dto -> {
                WorkoutSet set = new WorkoutSet();
                set.setTypeId(dto.getTypeId());
                set.setWeight(dto.getWeight());
                set.setReps(dto.getReps());
                set.setSession(session); // 設定關聯
                return set;
            })
            .collect(Collectors.toList());

        session.setSets(sets); // session 綁定這些 sets

        // 3. 存入資料庫（因為 cascade 設定所以 sets 也會一併儲存）
        workoutSessionRepository.save(session);

        return ResponseEntity.ok("Create session for user: " + user.getName());
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
    @PostMapping("/user/{id}/session/{sessionID}/set")
    public ResponseEntity<?> addWorkoutSetToSession(
            @PathVariable Long id, @PathVariable Long sessionID,
            @RequestBody WorkoutSetRequestDTO workoutSetRequestDTO
    ){
        WorkoutSession workoutSession = workoutSessionRepository.findByIdAndUser_Id(sessionID, id).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_FOR_USER_NOT_FOUND, "使用者或課表不存在, ID:" + id + "課表ID:" + sessionID, HttpStatus.NOT_FOUND)
        );
        WorkoutSet set = new WorkoutSet();
        set.setReps(workoutSetRequestDTO.getReps());
        set.setWeight(workoutSetRequestDTO.getWeight());
        set.setTypeId(workoutSetRequestDTO.getTypeId());
        set.setSession(workoutSession);
        workoutSetRepository.save(set);
        return ResponseEntity.ok("Create a new set with type:" + findWorkoutTypeNameByTypeId(set.getTypeId()));
    }   
    
    @Operation(summary = "取得使用者所有訓練紀錄", description = "依照使用者 ID 回傳所有訓練課表與細節")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/user/{id}/sessions")
    public ResponseEntity<?> getWorkoutSessions(@Parameter(description = "使用者ID", example = "1") @PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        List<WorkoutSessionDTO> sessionDTOs = workoutSessionRepository.findByUser_Id(user.getId()).stream()
            .map(session -> {
                List<WorkoutSetDTO> setDTOs = workoutSetRepository.findBySessionId(session.getId()).stream()
                    .map(set -> new WorkoutSetDTO(
                        set.getId(),
                        findWorkoutTypeNameByTypeId(set.getTypeId()), 
                        set.getWeight(), 
                        set.getReps()
                    ))
                    .collect(Collectors.toList());
                    return new WorkoutSessionDTO(
                        session.getId(), 
                        session.getDate(), 
                        session.getNote(), 
                        setDTOs);
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(sessionDTOs);
    }

    @Operation(summary = "取得使用者不同日期訓練課表的資訊", description = "依照使用者 ID 回傳不同日期訓練課表的內容和總重量")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/user/{id}/sessions/summary")
    public ResponseEntity<?> getWorkoutSessionSummaryDTO(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        List<WorkoutSessionSummaryDTO> summaryDTO = workoutSessionRepository.findByUser_Id(user.getId()).stream()
            .map(session -> {
                Integer totalWeihgt = calculateTotalWeight(workoutSetRepository.findBySessionId(session.getId()));
                return new WorkoutSessionSummaryDTO(session.getId(), session.getDate(), session.getNote(), totalWeihgt);
            }).collect(Collectors.toList());
        return ResponseEntity.ok(summaryDTO);
    }

    @Operation(summary = "取得使用者每週的訓練紀錄整理", description = "依照使用者 ID 回傳每週的訓練課表整理")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @GetMapping("/user/{id}/sessions/weekly-summary")
    public ResponseEntity<?> getWeeklySummary(
        @PathVariable Long id
    ){  
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        List<WeeklySummaryDTO> weeklySummaryDTOs = new ArrayList<>();
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for(WorkoutSession workoutSession:workoutSessionRepository.findByUser_Id(user.getId())){
            LocalDate date = LocalDate.parse(workoutSession.getDate(),dTimeFormatter);
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            Integer weekNumber = date.get(weekFields.weekOfWeekBasedYear());
            Integer year = date.getYear();
            String weekKey = year + "-W" + weekNumber;

            Integer sessionTotal = 0;
            for(WorkoutSet workoutSet:workoutSetRepository.findBySessionId(workoutSession.getId())){
                sessionTotal += workoutSet.getReps()*workoutSet.getWeight();
            }
            weeklySummaryDTOs.add(new WeeklySummaryDTO(weekKey, sessionTotal));
        }   
        return ResponseEntity.ok(weeklySummaryDTOs);

    }

    @Operation(summary = "刪除使用者的課表", description = "根據 課表ID 刪除課表")
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者或是找不到課表")
    @DeleteMapping("/user/{id}/session/{sessionID}")
    public ResponseEntity<?> deleteWorkoutSession(
        @PathVariable Long id, @PathVariable Long sessionID
    ){
        WorkoutSession session = workoutSessionRepository.findByIdAndUser_Id(sessionID, id).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_FOR_USER_NOT_FOUND, "使用者或課表不存在, ID:" + id + "課表ID:" + sessionID, HttpStatus.NOT_FOUND)
        );
        workoutSessionRepository.delete(session);
        return ResponseEntity.ok("removed session:" + sessionID);
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
    @DeleteMapping("/user/{userId}/session/{sessionId}/set/{setId}")
    public ResponseEntity<?> deleteWorkoutSet(
        @PathVariable Long userId,
        @PathVariable Long sessionId,
        @PathVariable Long setId
    ) {
        WorkoutSession session = workoutSessionRepository.findByIdAndUser_Id(sessionId, userId).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_FOR_USER_NOT_FOUND, "使用者或課表不存在, ID:" + userId + "課表ID:" + sessionId, HttpStatus.NOT_FOUND)
        );
        Optional<WorkoutSet> setToDelete = session.getSets().stream()
            .filter(set -> set.getId().equals(setId))
            .findFirst();

        if (setToDelete.isPresent()) {
            workoutSetRepository.delete(setToDelete.get());
            return ResponseEntity.ok("Deleted set ID: " + setId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set not found in session.");
        }
    }
    @Operation(
        summary = "更新訓練組資訊",
        description = "根據使用者 ID、課表 ID、訓練組 ID，更新指定訓練組的重量、次數與動作類型"
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
    @PutMapping("/user/{id}/session/{sessionId}/set/{setId}")
    public ResponseEntity<?> updateWorkoutSet(
        @PathVariable Long id,
        @PathVariable Long sessionId, 
        @PathVariable Long setId,
        @RequestBody WorkoutSetRequestDTO newSetDTO
    ){
        workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_FOR_USER_NOT_FOUND, "使用者或課表不存在, ID:" + id + "課表ID:" + sessionId, HttpStatus.NOT_FOUND)
        );
        WorkoutSet existingSet = workoutSetRepository.findById(setId).orElseThrow(() ->
            new ApiException(ApiErrorCode.SET_NOT_FOUND, "動作組數不存在 ID:" + setId, HttpStatus.NOT_FOUND)
        );
        existingSet.setTypeId(newSetDTO.getTypeId());
        existingSet.setReps(newSetDTO.getReps());
        existingSet.setWeight(newSetDTO.getWeight());
        workoutSetRepository.save(existingSet);
        return ResponseEntity.ok("Update set: " + setId);
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
    @GetMapping("/user/{id}/workouts/popular-types")
    public ResponseEntity<?> getPopularWorkoutTypes(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        Map<String, Integer> countMap = new HashMap<>();
            for(WorkoutSession session:workoutSessionRepository.findByUser_Id(user.getId())){
                for(WorkoutSet set:workoutSetRepository.findBySessionId(session.getId())){
                    String type = findWorkoutTypeNameByTypeId(set.getTypeId());
                    countMap.put(type, countMap.getOrDefault(type, 0) + 1);
                }
            }
            List<PopularTypesDTO> popularTypesDTOs = countMap.entrySet().stream()
                .map(entry -> new PopularTypesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
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
    @GetMapping("/user/{id}/workouts/progress")
    public ResponseEntity<?> getWorkoutProgressByType(
        @PathVariable Long id, @RequestParam Long typeId
    ){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        List<WorkoutProgressDTO> progressDTOs = workoutSessionRepository.findByUser_Id(user.getId()).stream().map(session -> {
            Integer totalWeight = workoutSetRepository.findBySessionId(session.getId()).stream()
            .filter(set -> set.getTypeId().equals(typeId))
            .mapToInt(set -> set.getReps() * set.getWeight())
            .sum();                   
            return new WorkoutProgressDTO(session.getDate(), totalWeight);
        })
        .filter(dto -> dto.getTotalWeight() > 0)
        .collect(Collectors.toList());
        return ResponseEntity.ok(progressDTOs);
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
    @GetMapping("/user/{id}/sessions/weekly-frequency")
    public ResponseEntity<?> getWeeklyWorkoutDays(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, Set<String>> weekToDates = workoutSessionRepository.findByUser_Id(user.getId())
        .stream()
        .collect(Collectors.groupingBy(
            session -> {
                LocalDate date = LocalDate.parse(session.getDate(), dateTimeFormatter);
                int week = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.getYear();
                return year + "-W" + week;
            },
            Collectors.mapping(WorkoutSession::getDate, Collectors.toSet())
        ));
        List<WeeklyFrequencyDTO> weeklyFrequencyDTOs = weekToDates.entrySet().stream()
            .map(entry -> {
                return new WeeklyFrequencyDTO(entry.getKey(), entry.getValue().size());
            }).collect(Collectors.toList());
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
    @GetMapping("/user/{id}/muscle-groups/total-weight")
    public ResponseEntity<?> getTotalWeightsByMuscleGroup(@PathVariable Long id){
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        Map<String, Integer> groupTotals = new HashMap<>();
        for(WorkoutSession session:user.getWorkoutSessions()){
            for(WorkoutSet set:session.getSets()){
                Integer total = set.getReps()*set.getWeight();
                String group = typeToMuscleGroup.get(findWorkoutTypeNameByTypeId(set.getTypeId()));
                if(group == null){
                    System.out.println(findWorkoutTypeNameByTypeId(set.getTypeId()) + "not been definde in current group.");
                } else{
                    groupTotals.put(group, groupTotals.getOrDefault(group, 0) + total);
                }
            }
        }
        List<MuscleGroupTotalDTO> muscleGroupTotalDTOs = groupTotals.entrySet().stream()
            .map(entry -> new MuscleGroupTotalDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(muscleGroupTotalDTOs);
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
    @PostMapping("/workout-types")
    public ResponseEntity<?> createWorkoutType(@RequestBody WorkoutTypeRequestDTO workoutTypeRequestDTO){
        WorkoutType saved =  new WorkoutType(
        workoutTypeRequestDTO.getMuscleGroup(),
        workoutTypeRequestDTO.getName()
        );
        workoutTypeRepository.save(saved);
        return ResponseEntity.ok(saved);
    }

    @Operation(
    summary = "取得所有訓練動作類型",
    description = "回傳目前系統中所有訓練動作類型，包含動作名稱與所對應的肌群名稱。"
)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功取得動作類型清單"),
        @ApiResponse(responseCode = "500", description = "伺服器錯誤")
    })
    @GetMapping("/workout-types")
    public ResponseEntity<?> getWorkoutType(){
        List<WorkoutTypeDTO> workoutTypeDTOs = workoutTypeRepository.findAll().stream()
            .map(workoutType -> new WorkoutTypeDTO(workoutType.getId(), workoutType.getName(), workoutType.getMuscleGroup()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(workoutTypeDTOs);  
    }

    @Operation(summary = "刪除訓練動作", description = "根據 動作ID 刪除動作")
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到動作")
    @DeleteMapping("/workout-types/{id}")
    public ResponseEntity<?> deleteWorkoutType(@Parameter(description = "動作ID", example = "1")
    @PathVariable Long id) {
        Optional<WorkoutType> typeOpt = workoutTypeRepository.findById(id);
        if (typeOpt.isPresent()) {
            workoutTypeRepository.deleteById(id);
            return ResponseEntity.ok("Deleted workout type: " + typeOpt.get().getName());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such workout type");
        }
    }
    
    //login section
    @Operation(summary = "使用者登入", description = "使用帳號密碼登入並回傳 JWT Token")
    @ApiResponse(responseCode = "200", description = "登入成功，回傳 token")
    @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
        // 建立驗證請求（Spring Security 會透過自定義 Provider 處理）
        Authentication authRequest = new UsernamePasswordAuthenticationToken(
            credentials.getName(), credentials.getPassword());
        //System.out.println("this is bcrypt" + userRepository.findByName(credentials.getName()).get().getPassword());
        // 驗證過程（會觸發 CustomAuthenticationProvider）
        Authentication authentication = authenticationManager.authenticate(authRequest);

        // 驗證成功 → 產生 JWT
        String token = JwtUtil.generateToken(credentials.getName());

        User user = userRepository.findByName(credentials.getName())
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
    }

    //previous without DTO protect
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    //     String name = credentials.get("name");
    //     String password = credentials.get("password");
    
    //     return userRepository.findByName(name)
    //         .filter(user -> user.getPassword().equals(password))
    //         .<ResponseEntity<?>>map(user -> {
    //             String token = JwtUtil.generateToken(name);
    //             return ResponseEntity.ok(Map.of("token", token, "userId", user.getId()));
    //         })
    //         .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials."));
    // }


    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
    //     String name = credentials.getName();
    //     String password = credentials.getPassword();

    //     // 1. 找使用者
    //     User user = userRepository.findByName(name)
    //         .orElseThrow(() -> new ApiException(
    //             ApiErrorCode.USER_NOT_FOUND,
    //             "此使用者不存在: " + name,
    //             HttpStatus.NOT_FOUND
    //         ));

    //     // 2. 驗證密碼
    //     if (!user.getPassword().equals(password)) {
    //         throw new ApiException(
    //             ApiErrorCode.PASSWORD_INCORRECT,
    //             "密碼錯誤，請重新輸入",
    //             HttpStatus.UNAUTHORIZED
    //         );
    //     }

    //     // 3. 發 token
    //     String token = JwtUtil.generateToken(user.getName());
    //     LoginResponseDTO response = new LoginResponseDTO(token, user.getId());

    //     return ResponseEntity.ok(response);
    // }
    


    //bottom
}
