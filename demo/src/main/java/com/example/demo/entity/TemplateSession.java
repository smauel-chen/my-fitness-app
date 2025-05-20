package com.example.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

// WorkoutTemplate.java
@Entity
public class TemplateSession {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDate plannedDate;

    @ManyToOne
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "workoutTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TemplateExercise> exercises = new ArrayList<>();

    public TemplateSession(){}

    public TemplateSession(String title, LocalDate plannedDate){
        this.title = title;
        this.plannedDate = plannedDate;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    
    public LocalDate getPlannedDate(){ return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate){ this.plannedDate = plannedDate; }

    public User getUser(){return user; }
    public void setUser(User user){ this.user = user; }

    public List<TemplateExercise> getExercises(){ return exercises; }
    public void setExercises(List<TemplateExercise> exercises){ this.exercises = exercises; }

}

