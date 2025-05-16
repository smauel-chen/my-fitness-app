package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// WorkoutTemplateSet.java
@Entity
public class TemplateSet {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer reps;
    private Integer weight;

    @ManyToOne
    @JsonBackReference
    private TemplateExercise exercise;

    public TemplateSet(){}

    public TemplateSet(Integer reps, Integer weight){
        this.reps = reps;
        this.weight = weight;
    }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public Integer getReps(){ return reps; }
    public void setReps(Integer reps){ this.reps = reps; }

    public Integer getWeight(){ return weight; }
    public void setWeight(Integer weight){ this.weight = weight; }
    
    public TemplateExercise getExercise(){ return exercise; }
    public void setExercise(TemplateExercise exercise){ this.exercise = exercise; } 
    
}
