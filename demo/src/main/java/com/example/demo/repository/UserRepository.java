package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    //User findByName(String name);  // for login

    Optional<User> findByName(String name);
}
