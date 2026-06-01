package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.AuthRequest;
import com.interview.platform.dto.AuthResponse;
import com.interview.platform.dto.RegisterRequest;
import com.interview.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Registration successful", response));
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
    }
}
