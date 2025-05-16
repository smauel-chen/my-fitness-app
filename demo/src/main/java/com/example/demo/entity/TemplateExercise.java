package com.example.demo.entity;

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


@Entity
public class TemplateExercise {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private WorkoutType workoutType; //獲取名字和對應的tag

    @ManyToOne
    @JsonBackReference
    private TemplateSession workoutTemplate;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TemplateSet> sets = new ArrayList<>();

    public TemplateExercise(){}

    public TemplateExercise(WorkoutType workoutType){
        this.workoutType = workoutType;
    }

    public Long getId(){ return this.id; }
    public void setId(Long id){ this.id = id; }
    
    public WorkoutType getWorkoutType(){ return this.workoutType; }
    public void setWorkoutType(WorkoutType workoutType){ this.workoutType = workoutType; }
    
    public TemplateSession getWorkoutTemplate(){ return this.workoutTemplate; }
    public void setWorkoutTemplate(TemplateSession workoutTemplate){ this.workoutTemplate = workoutTemplate; }

    public List<TemplateSet> getSets(){ return this.sets; }
    public void setSets(List<TemplateSet> sets){ this.sets = sets; } 

}

