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

import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.entity.WorkoutType;
//repository
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutSetRepository;
import com.example.demo.repository.WorkoutTypeRepository;

//security
import com.example.demo.config.JwtUtil;

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
        return workoutTypeRepository.findById(TypeId).map(WorkoutType::getName).orElse("Invalid TypeId");
    }

    private Integer calculateTotalWeight(List<WorkoutSet> sets) {
        return sets.stream()
                   .mapToInt(set -> set.getWeight() * set.getReps())
                   .sum();
    }
    
    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        User saved = userRepository.save(user);
        System.out.println("test: " + user.getName() +"/n");
        
        log.info("ID: {}, name: {}, age: {}", saved.getId(), saved.getName(), saved.getAge());
        return saved;
    }

    //PUT update
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            user.setName(updatedUser.getName());
            user.setAge(updatedUser.getAge());
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user."));
    }

    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }    

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok("Deleted user: " + user.getName());
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user."));
    }
        
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getXXX(@PathVariable Long id) {
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> ResponseEntity.ok(user)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }
    
    
    @PostMapping("/user/{id}/session")
    public ResponseEntity<?> addWorkoutSession(
            @PathVariable Long id,
            @RequestBody WorkoutSession workoutSession) {
                System.out.println("總共有幾組 set: " + workoutSession.getSets().size());
                log.info("Session sets數量：" + workoutSession.getSets().size());
                log.info("Session user是否為 null:" + (workoutSession.getUser() == null));
                
        return userRepository.findById(id).map(user -> {

            for(WorkoutSet set:workoutSession.getSets()){
                set.setSession(workoutSession);
            }
            workoutSession.setUser(user);
            workoutSessionRepository.save(workoutSession);
            return ResponseEntity.ok("Create session" + workoutSession);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a session"));
    }

    @PostMapping("/user/{id}/session/{sessionID}/set")
    public ResponseEntity<?> addWorkoutSetToSession(
            @PathVariable Long id, @PathVariable Long sessionID,
            @RequestBody WorkoutSet workoutSet){
        return workoutSessionRepository.findByIdAndUser_Id(sessionID, id).map(workoutSession -> {
            workoutSet.setSession(workoutSession);
            workoutSetRepository.save(workoutSet);    
            return ResponseEntity.ok("Create a new set with type:" + findWorkoutTypeNameByTypeId(workoutSet.getTypeId()));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a id or session"));
    }   

    @GetMapping("/user/{id}/sessions")
    public ResponseEntity<?> getWorkoutSessions(
            @PathVariable Long id){
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            List<WorkoutSession> sessions = workoutSessionRepository.findByUser_Id(user.getId());
            List<Map<String, Object>> ret = sessions.stream()
                .map(session -> {
                    Map<String, Object> sessionMap = new HashMap<>();
                    sessionMap.put("sessionId", session.getId());
                    sessionMap.put("date", session.getDate());
                    sessionMap.put("note", session.getNote());

                    List<Map<String, Object>> sets = session.getSets().stream()
                        .map(set -> {
                            Map<String, Object> setMap = new HashMap<>();
                            setMap.put("id", set.getId());
                            setMap.put("type", findWorkoutTypeNameByTypeId(set.getTypeId()));
                            setMap.put("weight", set.getWeight());
                            setMap.put("reps", set.getReps());
                            return setMap;
                        })
                        .collect(Collectors.toList());

                    sessionMap.put("sets", sets);
                    return sessionMap;
                })
                .collect(Collectors.toList());
                return ResponseEntity.ok(ret);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    @GetMapping("/user/{id}/sessions/summary")
    public ResponseEntity<?> getWorkoutSessionSummary(@PathVariable Long id) {

        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            List<Map<String, Object>> ret = user.getWorkoutSessions().stream().map(session -> {
                Integer totalWeight = calculateTotalWeight(session.getSets());
                Map<String, Object> entry = new HashMap<>();
                entry.put("sessionId", session.getId());
                entry.put("date", session.getDate());
                entry.put("note", session.getNote());
                entry.put("totalWeight", totalWeight);
                return entry;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(ret);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    @GetMapping("/user/{id}/sessions/weekly-summary")
    public ResponseEntity<?> getWeeklySummary(
        @PathVariable Long id
    ){  
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            Map<String, Integer> weeklyTotals = new HashMap<>();
            DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for(WorkoutSession workoutSession:user.getWorkoutSessions()){
                LocalDate date = LocalDate.parse(workoutSession.getDate(),dTimeFormatter);
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                Integer weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                Integer year = date.getYear();
                String weekKey = year + "-W" + weekNumber;

                Integer sessionTotal = 0;
                for(WorkoutSet workoutSet:workoutSession.getSets()){
                    sessionTotal += workoutSet.getReps()*workoutSet.getWeight();
                }
                weeklyTotals.put(weekKey, weeklyTotals.getOrDefault(weekKey, 0) + sessionTotal);    
            }   
            return ResponseEntity.ok(weeklyTotals);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    @DeleteMapping("/user/{id}/session/{sessionID}")
    public ResponseEntity<?> deleteWorkoutSession(
        @PathVariable Long id, @PathVariable Long sessionID
    ){
        return workoutSessionRepository.findByIdAndUser_Id(sessionID, id).map(session -> {
            workoutSessionRepository.delete(session);
            return ResponseEntity.ok("removed session:" + sessionID);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user or session"));
    }

    @DeleteMapping("/user/{userId}/session/{sessionId}/set/{setId}")
    public ResponseEntity<?> deleteWorkoutSet(
        @PathVariable Long userId,
        @PathVariable Long sessionId,
        @PathVariable Long setId
    ) {
        return workoutSessionRepository.findByIdAndUser_Id(sessionId, userId)
            .map(session -> {
                Optional<WorkoutSet> setToDelete = session.getSets().stream()
                    .filter(set -> set.getId().equals(setId))
                    .findFirst();
    
                if (setToDelete.isPresent()) {
                    workoutSetRepository.delete(setToDelete.get());
                    return ResponseEntity.ok("Deleted set ID: " + setId);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set not found in session.");
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such session or user."));
    }
    
    @PutMapping("/user/{id}/session/{sessionId}/set/{setId}")
    public ResponseEntity<?> updateWorkoutSet(
        @PathVariable Long id, @PathVariable Long sessionId, @PathVariable Long setId,
        @RequestBody WorkoutSet newSet
    ){
        return workoutSessionRepository.findByIdAndUser_Id(sessionId, id).map((session) -> {
            workoutSetRepository.findById(setId).map(existingSet -> {
                existingSet.setTypeId(id);
                existingSet.setReps(newSet.getReps());
                existingSet.setWeight(newSet.getWeight());
                workoutSetRepository.save(existingSet);
                return ResponseEntity.ok("Update set: " + existingSet.getId());
            }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a set"));

            return ResponseEntity.ok("Create newSet:" + findWorkoutTypeNameByTypeId(newSet.getTypeId()) + newSet.getId());
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user or session"));
    }

    @GetMapping("/user/{id}/workouts/popular-types")
    public ResponseEntity<?> getPopularWorkoutTypes(@PathVariable Long id){

        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            Map<String, Integer> ret = new HashMap<>();
            for(WorkoutSession session:user.getWorkoutSessions()){
                for(WorkoutSet set:session.getSets()){
                    String name = findWorkoutTypeNameByTypeId(set.getTypeId());
                    ret.put(name, ret.getOrDefault(name, 0) + 1);
                }
            }
            return ResponseEntity.ok(ret);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    @GetMapping("/user/{id}/workouts/progress")
    public ResponseEntity<?> getWorkoutProgressByType(
        @PathVariable Long id, @RequestParam Long TypeId
    ){
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            List<Map<String, Object>> ret = user.getWorkoutSessions().stream().map(session -> {
                Map<String, Object> entry = new HashMap<>();
                Integer toptalWeight = session.getSets().stream()
                    .filter(set -> set.getTypeId().equals(TypeId))
                    .mapToInt(set -> set.getReps() * set.getWeight())
                    .sum();
                entry.put("date", session.getDate());
                entry.put("totalWeight", toptalWeight);
                return entry;
            }).filter(entry -> (Integer)entry.get("totalWeight") > 0)
            .collect(Collectors.toList());
            return ResponseEntity.ok(ret);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user")); 
    }

    @GetMapping("/user/{id}/sessions/weekly-frequency")
    public ResponseEntity<?> getWeeklyWorkoutDays(@PathVariable Long id){

        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
            Map<String, Set<String>> weekToDays = new HashMap<>();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for(WorkoutSession session:user.getWorkoutSessions()){
                LocalDate date = LocalDate.parse(session.getDate(), dateTimeFormatter);
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                Integer weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                Integer year = date.getYear();
                String weekKey = year + "-W" + weekNumber;
    
                weekToDays.putIfAbsent(weekKey, new HashSet<>());
                weekToDays.get(weekKey).add(session.getDate());
            }
            Map<String, Integer> ret = new HashMap<>();
            for(Map.Entry<String, Set<String>> entry:weekToDays.entrySet()){
                ret.put(entry.getKey(), entry.getValue().size());
            }
            return ResponseEntity.ok(ret);            
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    @GetMapping("/user/{id}/muscle-groups/total-weight")
    public ResponseEntity<?> getTotalWeightsByMuscleGroup(@PathVariable Long id){
        return userRepository.findById(id).<ResponseEntity<?>>map(user -> {
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
            return ResponseEntity.ok(groupTotals);            
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such a user"));
    }

    //muscleType controll section
    @PostMapping("/workout-types")
    public ResponseEntity<?> createWorkoutType(@RequestBody WorkoutType workoutType){
        WorkoutType saved =  workoutTypeRepository.save(workoutType);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/workout-types")
    public ResponseEntity<?> getWorkoutType(){
        return ResponseEntity.ok(workoutTypeRepository.findAll());
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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String name = credentials.get("name");
        String password = credentials.get("password");
    
        return userRepository.findByName(name)
            .filter(user -> user.getPassword().equals(password))
            .<ResponseEntity<?>>map(user -> {
                String token = JwtUtil.generateToken(name);
                return ResponseEntity.ok(Map.of("token", token, "userId", user.getId()));
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials."));
    }
    //bottom
}
