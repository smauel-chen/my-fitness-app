package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.TemplateSession;

public interface TemplateSessionRepository extends JpaRepository<TemplateSession, Long> {
    List<TemplateSession> findByUserId(Long userId);
}
