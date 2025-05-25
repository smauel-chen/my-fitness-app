package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 對所有路徑生效
                    .allowedOrigins("http://localhost:5173","https://my-fitness-app-brown.vercel.app") // 允許哪個前端網址
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的方法
                    .allowedHeaders("*") // 允許的header
                    .allowCredentials(true); // 允許攜帶cookie或授權資訊（例如token）
            }
        };
    }
}
