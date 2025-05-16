package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TemplateSet;

public interface TemplateSetRepository extends JpaRepository<TemplateSet, Long> {

}