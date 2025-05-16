package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


@Entity
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer reps;
    private Integer weight;

    @ManyToOne
    @JsonBackReference
    private WorkoutExercise exercise;

    // @ManyToOne
    // @JoinColumn(name = "session_id")
    // @JsonBackReference
    // private WorkoutSession session; // link to session

    public WorkoutExercise getExercise() {
        return exercise;
    }

    public void setExercise(WorkoutExercise exercise) {
        this.exercise = exercise;
    }


    public WorkoutSet(){}

    public WorkoutSet(Integer reps, Integer weight){
        this.reps = reps;
        this.weight = weight;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }
    
    public void setReps(Integer reps){
        this.reps = reps;
    }

    public Integer getReps(){
        return reps;
    }

    public void setWeight(Integer weight){
        this.weight = weight;
    }

    public Integer getWeight(){
        return weight;
    }
}
