package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.config.JwtUtil;

//DTO
import com.example.demo.dto.EnrollRequestDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdatedRequestDTO;

//exception
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ApiErrorCode;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//security
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserService( UserRepository userRepository, 
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public void enrollUser(EnrollRequestDTO enrollRequestDTO) {
        if (userRepository.findByName(enrollRequestDTO.getName()).isPresent()) {
            throw new ApiException(ApiErrorCode.USER_ALREADY_EXISTS, "使用者名稱已存在", HttpStatus.BAD_REQUEST);
        }
        String encryptedPassword = passwordEncoder.encode(enrollRequestDTO.getPassword());
        User user = new User(enrollRequestDTO.getName(), enrollRequestDTO.getAge(), encryptedPassword);
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getName(), user.getAge()))
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserUpdatedRequestDTO userUpdatedRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        user.setName(userUpdatedRequestDTO.getName());
        user.setAge(userUpdatedRequestDTO.getAge());
        userRepository.save(user);
        return new UserDTO(user.getId(), user.getName(), user.getAge());
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在, ＩＤ:" + id, HttpStatus.NOT_FOUND)
        );
        userRepository.delete(user);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
            new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在 ID：" + id, HttpStatus.NOT_FOUND)
        );
        return new UserDTO(user.getId(), user.getName(), user.getAge());
    }

    public LoginResponseDTO login(LoginRequestDTO credentials){
        // 建立驗證請求（Spring Security 會透過自定義 Provider 處理）
        Authentication authRequest = new UsernamePasswordAuthenticationToken(
            credentials.getName(), credentials.getPassword());
        // 驗證過程（會觸發 CustomAuthenticationProvider）
        authenticationManager.authenticate(authRequest);

        // 驗證成功 → 產生 JWT
        String token = JwtUtil.generateToken(credentials.getName());

        User user = userRepository.findByName(credentials.getName())
            .orElseThrow(() -> new ApiException(ApiErrorCode.USER_NOT_FOUND, "使用者不存在", HttpStatus.NOT_FOUND));
        return new LoginResponseDTO(token, user.getId());
    }
}