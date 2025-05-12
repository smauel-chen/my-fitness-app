package com.example.demo.dto;

public class PopularTypesDTO {
    private String type;
    private Integer count;

    public PopularTypesDTO(){}

    public PopularTypesDTO(String type, Integer count) {
        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public Integer getCount() {
        return count;
    }

    public void setType(String type){this.type = type;}
    public void setCount(Integer count){this.count = count;}
} 
