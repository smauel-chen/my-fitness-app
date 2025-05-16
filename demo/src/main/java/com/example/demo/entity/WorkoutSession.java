package com.example.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;


@Entity
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; 
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "user_id")  // 對應到 user 的主鍵
    @JsonBackReference
    private User user; 

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutExercise> exercises = new ArrayList<>();
    

    public void setUser(User user) {
        this.user = user;
    }

    public WorkoutSession(){}

    public WorkoutSession(LocalDate date, String title){
        this.date = date;
        this.title = title;
    }

    public void addExercise(WorkoutExercise exercise) {
        exercise.setSession(this);
        this.exercises.add(exercise);
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setNote(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    
    public void setDate(LocalDate date){
        this.date = date;
    }

    public LocalDate getDate(){
        return date;
    }
}
