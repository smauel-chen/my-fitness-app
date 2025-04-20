package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiErrorCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 這是 Spring Security 的入口點：給定 username 要回傳 UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "帳號不存在：" + username, HttpStatus.NOT_FOUND));

        return new org.springframework.security.core.userdetails.User(
            user.getName(), // username
            user.getPassword(), // password（可以加密過）
            Collections.emptyList() // 權限清單（目前不處理）
        );
    }
}
