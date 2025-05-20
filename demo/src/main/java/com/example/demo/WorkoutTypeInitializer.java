package com.example.demo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.entity.WorkoutType;
import com.example.demo.repository.WorkoutTypeRepository;

@Component
public class WorkoutTypeInitializer implements CommandLineRunner {

    private final WorkoutTypeRepository workoutTypeRepository;

    public WorkoutTypeInitializer(WorkoutTypeRepository workoutTypeRepository) {
        this.workoutTypeRepository = workoutTypeRepository;
    }

    @Override
    public void run(String... args) {

        System.out.println("🔧 檢查預設 WorkoutType 是否存在...");

        List<WorkoutType> defaultTypes = List.of(
            new WorkoutType("啞鈴臥推", "Chest", List.of("Shoulder","Tricep","Dumbbell"), false),
            new WorkoutType("槓鈴臥推", "Chest", List.of("Shoulder","Tricep","Barbell"), false),
            new WorkoutType("深蹲", "Leg", List.of("Barbell"), false),
            new WorkoutType("腿推機", "Leg", List.of("Machine"), false),
            new WorkoutType("引體向上", "Back", List.of("Bodyweight"), false),
            new WorkoutType("滑輪下拉", "Back", List.of("Cable"), false),
            new WorkoutType("二頭彎舉", "Bicep", List.of("Dumbbell"), false),
            new WorkoutType("繩索下拉", "Tricep", List.of("Cable"), false)
        );

        for (WorkoutType type : defaultTypes) {
            if (!workoutTypeRepository.existsByName(type.getName())) {
                workoutTypeRepository.save(type);
                System.out.println("✅ 新增預設動作：" + type.getName());
            }
        }
        System.out.println("🏁 預設動作檢查完成");
    }
}
