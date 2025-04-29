package com.example.demo.entity;

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

    private String date;
    private String note; // 當次訓練備注
    
    @ManyToOne
    @JoinColumn(name = "user_id")  // 對應到 user 的主鍵
    @JsonBackReference
    private User user; 

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WorkoutSet> sets;
    

    public void setUser(User user) {
        this.user = user;
    }

    public WorkoutSession(){
        this.sets = new ArrayList<>();
    }

    public WorkoutSession(String date, String note){
        this.date = date;
        this.note = note;
        this.sets = new ArrayList<>();
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

    public void setNote(String note){
        this.note = note;
    }

    public String getNote(){
        return note;
    }
    
    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public void setSets(List<WorkoutSet> sets){
        this.sets = sets;
    }

    public List<WorkoutSet> getSets(){
        return sets;
    }
}
