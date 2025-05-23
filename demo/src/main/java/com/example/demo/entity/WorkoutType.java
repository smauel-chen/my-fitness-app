package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class WorkoutType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String mainTag;
    
    @ElementCollection
    private List<String> secondaryTags = new ArrayList<>();

    private boolean isCustom = true; // 預設為 true，代表使用者新增

    public WorkoutType(){}

    public WorkoutType(String name, String mainTag, List<String> secondaryTags){
        this.name = name;
        this.mainTag = mainTag;
        this.secondaryTags = (secondaryTags != null) ? secondaryTags : new ArrayList<>();
    }

    public WorkoutType(String name, String mainTag, List<String> secondaryTags, Boolean isCustom){
        this.name = name;
        this.mainTag = mainTag;
        this.secondaryTags = (secondaryTags != null) ? secondaryTags : new ArrayList<>();
        this.isCustom = isCustom;
    }

    public void setMainTag(String mainTag){ this.mainTag = mainTag; }
    public String getMainTag(){ return mainTag; }

    public void setSecondaryTags(List<String> secondaryTags){ this.secondaryTags = secondaryTags; }
    public List<String> getSecondaryTags(){ return secondaryTags; }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Boolean getIsCusetom() { return isCustom; }
    public void setIsCustom( Boolean isCustom ){ this.isCustom = isCustom; }
}
