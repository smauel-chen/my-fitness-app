package com.example.demo.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BodyWeightRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double weight;

    @JsonIgnore
    @ManyToOne
    private User user;

    public BodyWeightRecord() {}

    public BodyWeightRecord(LocalDate date, Double weight, User user) {
        this.date = date;
        this.weight = weight;
        this.user = user;
    }

    public void setDate(LocalDate date){ this.date = date; }
    public LocalDate getDate(){ return date; }

    public void setWeight(Double weight){ this.weight = weight; }
    public Double getWeight(){ return weight; }

    public void setUser(User user){ this.user = user; }
    public User getUser(){ return user; }

    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
}

