package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.UserDTO;
import com.interview.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Management", description = "Admin operations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserService userService;
    
    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse(true, "Users retrieved", users));
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User deleted", null));
    }
    
    @PostMapping("/users/{id}/block")
    @Operation(summary = "Block user")
    public ResponseEntity<ApiResponse> blockUser(@PathVariable Long id) {
        userService.blockUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User blocked", null));
    }
    
    @PostMapping("/users/{id}/unblock")
    @Operation(summary = "Unblock user")
    public ResponseEntity<ApiResponse> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
        return ResponseEntity.ok(new ApiResponse(true, "User unblocked", null));
    }
}
