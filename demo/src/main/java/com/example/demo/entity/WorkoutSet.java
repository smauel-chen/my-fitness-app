package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;


@Entity
public class WorkoutSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long typeId;
    private Integer reps;
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private WorkoutSession session; // link to session

    public void setSession(WorkoutSession session){
        this.session = session;
    }

    public WorkoutSession getSession(){
        return session;
    }

    public WorkoutSet(){}

    public WorkoutSet(Long typeId, Integer reps, Integer weight){
        this.typeId = typeId;
        this.reps = reps;
        this.weight = weight;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setTypeId(Long typeId){
        this.typeId = typeId;
    }

    public Long getTypeId(){
        return typeId;
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
