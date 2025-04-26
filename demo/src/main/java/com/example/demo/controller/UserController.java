package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//service
import com.example.demo.service.UserService;

//dto
import com.example.demo.dto.ApiSuccessResponseDTO;
import com.example.demo.dto.EnrollRequestDTO;
import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdatedRequestDTO;


//swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;



@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class UserController {

    private final UserService userService;

    UserController( UserService userService ){
        this.userService = userService;
    }

    @Operation(summary = "註冊新使用者", description = "傳入使用者資料（名稱、密碼、年齡）來建立新帳號")
    @ApiResponse(responseCode = "201", description = "註冊成功")
    @ApiResponse(responseCode = "400", description = "資料格式錯誤")
    @PostMapping("/user")
    public ResponseEntity<ApiSuccessResponseDTO> enrollUser(@RequestBody EnrollRequestDTO enrollRequestDTO) {
        userService.enrollUser(enrollRequestDTO);
        ApiSuccessResponseDTO response = new ApiSuccessResponseDTO(201, "註冊成功！");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    

    //PUT update
    @Operation(summary = "更新使用者資料", description = "更新使用者名稱與年齡")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")    
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(
        @Parameter(description = "要更新的使用者ID", example = "1") @PathVariable Long id, @RequestBody UserUpdatedRequestDTO userUpdatedRequestDTO) {
        UserDTO userDTO = userService.updateUser(id, userUpdatedRequestDTO);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "取得所有使用者", description = "回傳系統中所有使用者的基本資料清單（不含密碼）")
    @ApiResponse(responseCode = "200", description = "成功取得使用者清單")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = userService.getAllUsers();
        return ResponseEntity.ok(userDTOs);
    }    
    
    @Operation(summary = "刪除使用者", description = "根據 ID 刪除使用者")
    @ApiResponse(responseCode = "200", description = "刪除成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@Parameter(description = "要刪除的使用者ID", example = "1")@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted user: " + id);
    }

    @Operation(summary = "取得使用者資訊", description = "根據 ID 回傳使用者名稱與年齡")
    @ApiResponse(responseCode = "200", description = "取得成功")
    @ApiResponse(responseCode = "404", description = "找不到使用者")    
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUserById(
        @Parameter(description = "要獲取的使用者ID", example = "1")    @PathVariable Long id
    ) {
        UserDTO dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }
    
    //login section
    @Operation(summary = "使用者登入", description = "使用帳號密碼登入並回傳 JWT Token")
    @ApiResponse(responseCode = "200", description = "登入成功，回傳 token")
    @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
        LoginResponseDTO response = userService.login(credentials);
        return ResponseEntity.ok(response);
    }

    //previous without DTO protect
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    //     String name = credentials.get("name");
    //     String password = credentials.get("password");
    
    //     return userRepository.findByName(name)
    //         .filter(user -> user.getPassword().equals(password))
    //         .<ResponseEntity<?>>map(user -> {
    //             String token = JwtUtil.generateToken(name);
    //             return ResponseEntity.ok(Map.of("token", token, "userId", user.getId()));
    //         })
    //         .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials."));
    // }


    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
    //     String name = credentials.getName();
    //     String password = credentials.getPassword();

    //     // 1. 找使用者
    //     User user = userRepository.findByName(name)
    //         .orElseThrow(() -> new ApiException(
    //             ApiErrorCode.USER_NOT_FOUND,
    //             "此使用者不存在: " + name,
    //             HttpStatus.NOT_FOUND
    //         ));

    //     // 2. 驗證密碼
    //     if (!user.getPassword().equals(password)) {
    //         throw new ApiException(
    //             ApiErrorCode.PASSWORD_INCORRECT,
    //             "密碼錯誤，請重新輸入",
    //             HttpStatus.UNAUTHORIZED
    //         );
    //     }

    //     // 3. 發 token
    //     String token = JwtUtil.generateToken(user.getName());
    //     LoginResponseDTO response = new LoginResponseDTO(token, user.getId());

    //     return ResponseEntity.ok(response);
    // }
    
}
