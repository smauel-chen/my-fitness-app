package com.example.demo.entity;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private WorkoutSession session;

    @ManyToOne
    private WorkoutType workoutType; // 動作類型（如臥推、肩推）

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutSet> sets = new ArrayList<>();

    public WorkoutExercise() {}

    public WorkoutExercise(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }

    public void addSet(WorkoutSet set) {
        set.setExercise(this);
        this.sets.add(set); 
    }
}
