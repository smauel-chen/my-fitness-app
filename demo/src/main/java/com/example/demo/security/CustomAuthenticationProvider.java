package com.example.demo.security;

import com.example.demo.exception.ApiErrorCode;
import com.example.demo.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {



    private CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    CustomAuthenticationProvider(
        PasswordEncoder passwordEncoder,
        CustomUserDetailsService userDetailsService
    ){
            this.userDetailsService = userDetailsService;
            this.passwordEncoder = passwordEncoder;
    }

    // 用來比對帳號密碼
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        // 目前先用明碼比對（實務上應該做 hash 驗證）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ApiErrorCode.INVALID_CREDENTIALS, "密碼錯誤", HttpStatus.UNAUTHORIZED);
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    // 限定支援的驗證型別（這裡只支援 UsernamePasswordAuthenticationToken）
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
