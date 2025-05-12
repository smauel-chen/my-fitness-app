package com.example.demo.service;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MuscleGroupTotalDTO;
import com.example.demo.dto.PopularTypesDTO;
import com.example.demo.dto.TestDTO;
import com.example.demo.dto.WeeklyFrequencyDTO;
import com.example.demo.dto.WeeklySummaryDTO;
import com.example.demo.dto.WorkoutProgressDTO;
import com.example.demo.dto.WorkoutSessionDTO;
import com.example.demo.dto.WorkoutSessionRequestDTO;
import com.example.demo.dto.WorkoutSessionSummaryDTO;
import com.example.demo.dto.WorkoutSetDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.WorkoutSession;
import com.example.demo.entity.WorkoutSet;
import com.example.demo.entity.WorkoutType;
import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkoutSessionRepository;
import com.example.demo.repository.WorkoutTypeRepository;

@Service
public class WorkoutSessionService {
    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;
    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutSessionService(
        WorkoutSessionRepository workoutSessionRepository,
        UserRepository userRepository,
        WorkoutTypeRepository workoutTypeRepository
    ) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
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

    public void addSessionTest(Long id, TestDTO testDTO){
        
        System.out.println("傳入的session" + testDTO.getDate());
        System.out.println("傳入的session" + testDTO.getNote());
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ id, HttpStatus.NOT_FOUND));
        WorkoutSession session = new WorkoutSession();
        session.setDate(testDTO.getDate());
        session.setNote(testDTO.getNote());
        session.setUser(user);
        if (testDTO.getSets() == null) {
            throw new ApiException(ApiErrorCode.BAD_REQUEST, "缺少訓練組內容", HttpStatus.BAD_REQUEST);
        }
        System.out.println("綁定set之前" + session.getNote());
        System.out.println("綁定set之前" + session.getDate());
        
        List<WorkoutSet> sets = testDTO.getSets().stream()
            .map(dto -> {
                WorkoutSet set = new WorkoutSet();
                set.setTypeId(dto.getTypeId());
                set.setReps(dto.getReps());
                set.setWeight(dto.getWeight());
                set.setSession(session); // 設定關聯
                return set;
            })
            .collect(Collectors.toList());
        session.setSets(sets); 
    }

    public void addSession(Long id, WorkoutSessionRequestDTO workoutSessionRequestDTO){
        
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ id, HttpStatus.NOT_FOUND));
        // 1. 建立 WorkoutSession 物件，並設定基本欄位與 user
        WorkoutSession session = new WorkoutSession();
        session.setDate(workoutSessionRequestDTO.getDate());
        session.setNote(workoutSessionRequestDTO.getNote());
        session.setUser(user);
        // 2. 建立每一組 set 的實體物件並與 session 關聯

        List<WorkoutSet> sets = workoutSessionRequestDTO.getSets().stream()
            .map(dto -> {
                if(dto.getTypeId() == null || dto.getReps() == null || dto.getWeight() == null){
                    throw new ApiException(ApiErrorCode.BAD_REQUEST, "缺少訓練組內容",  HttpStatus.BAD_REQUEST);
                }
                WorkoutSet set = new WorkoutSet();
                set.setTypeId(dto.getTypeId());
                set.setReps(dto.getReps());
                set.setWeight(dto.getWeight());
                set.setSession(session); // 設定關聯
                return set;
            })
            .collect(Collectors.toList());
        session.setSets(sets); // session 綁定這些 sets
        // 3. 存入資料庫（因為 cascade 設定所以 sets 也會一併儲存）
        workoutSessionRepository.save(session);
    }

    public List<Long> getAllSessionIds(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者", HttpStatus.NOT_FOUND));
    
        List<Long> sessionIds = workoutSessionRepository.findByUser_Id(userId)
            .stream()
            .map(WorkoutSession::getId)
            .collect(Collectors.toList());
        return sessionIds;
    }


    public List<WorkoutSessionDTO> getAllSession(Long id){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, Id:"+ id, HttpStatus.NOT_FOUND));
        List<WorkoutSessionDTO> sessionDTOs = workoutSessionRepository.findByUser_Id(id).stream()
            .map(session -> {
                List<WorkoutSetDTO> setDTOs = session.getSets().stream()
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
        return sessionDTOs;
    }

    public void deleteSession(Long id, Long sessionId){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, id:" + id, HttpStatus.NOT_FOUND));
        WorkoutSession session = workoutSessionRepository.findByIdAndUser_Id(sessionId, id).orElseThrow(() -> 
            new ApiException(ApiErrorCode.SESSION_NOT_FOUND, "課表不存在 Id:" + sessionId, HttpStatus.NOT_FOUND)
        );
        workoutSessionRepository.delete(session);
    }

    public List<WorkoutSessionSummaryDTO> getWorkoutSessionSummary(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, id:" + id, HttpStatus.NOT_FOUND));
        List<WorkoutSessionSummaryDTO> summaryDTO = workoutSessionRepository.findByUser_Id(user.getId()).stream()
            .map(session -> {
                Integer totalWeihgt = calculateTotalWeight(session.getSets());
                return new WorkoutSessionSummaryDTO(session.getId(), session.getDate(), session.getNote(), totalWeihgt);
            }).collect(Collectors.toList());
        return summaryDTO;
    }

    public List<WeeklySummaryDTO> getWeeklySummary(Long id){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, id:" + id, HttpStatus.NOT_FOUND));
        DateTimeFormatter dTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Integer> weekToTotalWeight = new HashMap<>();
        for(WorkoutSession workoutSession:workoutSessionRepository.findByUser_Id(id)){
            LocalDate date = LocalDate.parse(workoutSession.getDate(),dTimeFormatter);
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            Integer weekNumber = date.get(weekFields.weekOfWeekBasedYear());
            Integer year = date.getYear();
            String weekKey = year + "-W" + weekNumber;

            Integer sessionTotal = 0;
            for(WorkoutSet workoutSet:workoutSession.getSets()){
                sessionTotal += workoutSet.getReps()*workoutSet.getWeight();
            }
            weekToTotalWeight.merge(weekKey, sessionTotal, Integer::sum);
        }   
        return weekToTotalWeight.entrySet().stream()
            .map(entry -> new WeeklySummaryDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    public List<WeeklyFrequencyDTO> getWeeklyFrequency(Long id){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "找不到使用者, id:" + id, HttpStatus.NOT_FOUND));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, Set<String>> weekToDates = workoutSessionRepository.findByUser_Id(id)
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
        return weeklyFrequencyDTOs;
    }

    public List<MuscleGroupTotalDTO> getTotalWeightsByMuscleGroup(Long id){//
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND)
        );  
        Map<String, Integer> groupTotals = new HashMap<>();
        for(WorkoutSession session:user.getWorkoutSessions()){
            for(WorkoutSet set:session.getSets()){
                Integer total = set.getReps()*set.getWeight();
                WorkoutType type = workoutTypeRepository.findById(set.getTypeId()).orElseThrow(() -> 
                    new ApiException(ApiErrorCode.TYPE_NOT_FOUND, "找不到對應動作類型", HttpStatus.NOT_FOUND)
                );
                groupTotals.put(type.getMuscleGroup(), groupTotals.getOrDefault(type.getMuscleGroup(), 0) + total);
            }
        }
        List<MuscleGroupTotalDTO> muscleGroupTotalDTOs = groupTotals.entrySet().stream()
            .map(entry -> new MuscleGroupTotalDTO(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(MuscleGroupTotalDTO::getTotalWeight).reversed()) 
            .collect(Collectors.toList());
        return muscleGroupTotalDTOs;
    }


    public List<PopularTypesDTO> getPopularWorkoutType(Long id){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND));  
        Map<String, Integer> countMap = new HashMap<>();
        for(WorkoutSession session:workoutSessionRepository.findByUser_Id(id)){
            for(WorkoutSet set:session.getSets()){
                String type = findWorkoutTypeNameByTypeId(set.getTypeId());
                countMap.put(type, countMap.getOrDefault(type, 0) + 1);
            }
        }
        List<PopularTypesDTO> popularTypesDTOs = countMap.entrySet().stream()
            .map(entry -> new PopularTypesDTO(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        return popularTypesDTOs;
    }

    public List<WorkoutProgressDTO> getWorkoutProgressByType(Long id, Long typeId){
        userRepository.findById(id).orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在,ID:" + id, HttpStatus.NOT_FOUND));  
        List<WorkoutProgressDTO> progressDTOs = workoutSessionRepository.findByUser_Id(id).stream().map(session -> {
            Integer totalWeight = session.getSets().stream()
            .filter(set -> set.getTypeId().equals(typeId))
            .mapToInt(set -> set.getReps() * set.getWeight())
            .sum();                   
            return new WorkoutProgressDTO(session.getDate(), totalWeight);
        })
        .filter(dto -> dto.getTotalWeight() > 0)
        .collect(Collectors.toList());
        return progressDTOs;
    }
}
