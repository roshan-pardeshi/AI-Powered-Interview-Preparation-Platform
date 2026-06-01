package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.JobMatchDTO;
import com.interview.platform.dto.JobMatchRequest;
import com.interview.platform.service.JobMatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/job-match")
@Tag(name = "Job Matching", description = "Resume to job description matching")
@RequiredArgsConstructor
public class JobMatchController {
    
    private final JobMatchService jobMatchService;
    
    @PostMapping("/analyze")
    @Operation(summary = "Analyze job match")
    public ResponseEntity<ApiResponse> analyzeJobMatch(
            @RequestBody JobMatchRequest request,
            Authentication auth) throws IOException {
        Long userId = Long.parseLong(auth.getName());
        JobMatchDTO jobMatch = jobMatchService.matchJobWithResume(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Job match analyzed", jobMatch));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get job match details")
    public ResponseEntity<ApiResponse> getJobMatch(@PathVariable Long id) {
        JobMatchDTO jobMatch = jobMatchService.getJobMatch(id);
        return ResponseEntity.ok(new ApiResponse(true, "Job match retrieved", jobMatch));
    }
    
    @GetMapping("/my-matches")
    @Operation(summary = "Get user's job matches")
    public ResponseEntity<ApiResponse> getUserJobMatches(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        List<JobMatchDTO> matches = jobMatchService.getUserJobMatches(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Job matches retrieved", matches));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job match")
    public ResponseEntity<ApiResponse> deleteJobMatch(@PathVariable Long id) {
        jobMatchService.deleteJobMatch(id);
        return ResponseEntity.ok(new ApiResponse(true, "Job match deleted", null));
    }
}
