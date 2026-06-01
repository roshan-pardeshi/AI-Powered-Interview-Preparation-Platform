package com.interview.platform.controller;

import com.interview.platform.dto.AnalyticsDTO;
import com.interview.platform.dto.ApiResponse;
import com.interview.platform.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "User analytics and statistics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/my-analytics")
    @Operation(summary = "Get user analytics")
    public ResponseEntity<ApiResponse> getUserAnalytics(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        AnalyticsDTO analytics = analyticsService.getUserAnalytics(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Analytics retrieved", analytics));
    }
}
