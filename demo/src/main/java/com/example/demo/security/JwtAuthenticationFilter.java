package com.example.demo.security;

import com.example.demo.config.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = JwtUtil.validateToken(token);
                String username = claims.getSubject();

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = new User(username, "", Collections.emptyList());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // 無效 Token 不設定認證
                    // ❗改成這樣：直接手動寫回 JSON 錯誤
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("""
                    {
                        "status": 401,
                        "message": "JWT 無效或過期",
                        "path": "%s",
                        "name": "TOKEN_INVALID"
                    }
                """.formatted(request.getRequestURI()));
                return; // 終止 Filter 鏈
                // Spring 的處理順序
                // Filter（過濾器）
                // 最早期進入處理流程，像是 JwtAuthenticationFilter
                // （你寫的就是這種，它來自 OncePerRequestFilter）

                // DispatcherServlet（分發器）
                // 負責將請求轉交到對應的 Controller、Handler、Resolver 等。

                // Controller / Service / Repository 真正的應用邏輯與處理。

                // ExceptionHandler（如 @ControllerAdvice） 👉 只有 Controller 執行階段發生錯誤時 才會進來這層
                //throw new ApiException(ApiErrorCode.TOKEN_INVALID, "JWT 無效或過期", HttpStatus.UNAUTHORIZED);
            }
        }

        filterChain.doFilter(request, response);
    }
}