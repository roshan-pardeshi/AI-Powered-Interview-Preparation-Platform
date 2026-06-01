package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.ResumeDTO;
import com.interview.platform.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@Tag(name = "Resume Management", description = "Resume upload and analysis")
@RequiredArgsConstructor
public class ResumeController {
    
    private final ResumeService resumeService;
    
    @PostMapping("/upload")
    @Operation(summary = "Upload resume")
    public ResponseEntity<ApiResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {
        Long userId = Long.parseLong(auth.getName());
        ResumeDTO resume = resumeService.uploadResume(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Resume uploaded successfully", resume));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get resume details")
    public ResponseEntity<ApiResponse> getResume(@PathVariable Long id) {
        ResumeDTO resume = resumeService.getResume(id);
        return ResponseEntity.ok(new ApiResponse(true, "Resume retrieved", resume));
    }
    
    @GetMapping("/my-resumes")
    @Operation(summary = "Get user's resumes")
    public ResponseEntity<ApiResponse> getUserResumes(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        List<ResumeDTO> resumes = resumeService.getUserResumes(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Resumes retrieved", resumes));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume")
    public ResponseEntity<ApiResponse> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(new ApiResponse(true, "Resume deleted", null));
    }
}
