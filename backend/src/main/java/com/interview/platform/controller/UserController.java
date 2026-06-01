package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.UserDTO;
import com.interview.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Management", description = "User profile and settings")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<ApiResponse> getProfile(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved", user));
    }
    
    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody UserDTO userDTO, Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        UserDTO updated = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated", updated));
    }
    
    @PostMapping("/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully", null));
    }
}
