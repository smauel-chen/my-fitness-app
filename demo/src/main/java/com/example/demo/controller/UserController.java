package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

//entity
import com.example.demo.entity.*;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
//repository
import com.example.demo.repository.*;

import jakarta.validation.Valid;

//security
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;
//dto
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserRequestDTO;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {
    @Autowired
    private final UserRepository userRepository;
    
    private final WorkoutSessionRepository workoutSessionRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final WorkoutTypeRepository workoutTypeRepository;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
                    WorkoutTypeRepository workoutTypeRepository) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.workoutTypeRepository = workoutTypeRepository;
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
    
    @PostMapping("/user")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = new User(
            userRequestDTO.getName(),
            userRequestDTO.getAge(),
            userRequestDTO.getPassword()
        );
    
        User saved = userRepository.save(user);
        log.info("Created user: ID={}, Name={}, Age={}", saved.getId(), saved.getName(), saved.getAge());
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }
    

    //PUT update
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdatedRequestDTO userUpdatedRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        user.setName(userUpdatedRequestDTO.getName());
        user.setAge(userUpdatedRequestDTO.getAge());
        userRepository.save(user);
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getName(), user.getAge()));
    }

    
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userRepository.findAll().stream()
            .map(user -> new UserDTO(user.getId(), user.getName(), user.getAge()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }    

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        userRepository.delete(user);
        return ResponseEntity.ok("Deleted user: " + user.getName());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
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

    @GetMapping("/user/{id}/sessions")
    public ResponseEntity<?> getWorkoutSessions( @PathVariable Long id){
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
    @PostMapping("/workout-types")
    public ResponseEntity<?> createWorkoutType(@RequestBody WorkoutTypeRequestDTO workoutTypeRequestDTO){
        WorkoutType saved =  new WorkoutType(
        workoutTypeRequestDTO.getMuscleGroup(),
        workoutTypeRequestDTO.getName()
        );
        workoutTypeRepository.save(saved);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/workout-types")
    public ResponseEntity<?> getWorkoutType(){
        List<WorkoutTypeDTO> workoutTypeDTOs = workoutTypeRepository.findAll().stream()
            .map(workoutType -> new WorkoutTypeDTO(workoutType.getId(), workoutType.getName(), workoutType.getMuscleGroup()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(workoutTypeDTOs);  
    }

    @DeleteMapping("/workout-types/{id}")
    public ResponseEntity<?> deleteWorkoutType(@PathVariable Long id) {
        Optional<WorkoutType> typeOpt = workoutTypeRepository.findById(id);
        if (typeOpt.isPresent()) {
            workoutTypeRepository.deleteById(id);
            return ResponseEntity.ok("Deleted workout type: " + typeOpt.get().getName());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such workout type");
        }
    }
    

    //login section
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {

        return userRepository.findByName(credentials.getName())
            .filter(user -> user.getPassword().equals(credentials.getPassword()))
            .<ResponseEntity<?>>map(user -> {
                String token = JwtUtil.generateToken(user.getName());
                return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
            })
            .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_CREDENTIALS, "帳號密碼錯誤", HttpStatus.UNAUTHORIZED));
    }

    //bottom
}
