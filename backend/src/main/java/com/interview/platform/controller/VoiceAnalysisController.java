package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.VoiceAnalysisDTO;
import com.interview.platform.service.VoiceAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/voice")
@Tag(name = "Voice Analysis", description = "Voice analysis and speech recognition")
@RequiredArgsConstructor
public class VoiceAnalysisController {
    
    private final VoiceAnalysisService voiceAnalysisService;
    
    @PostMapping("/analyze")
    @Operation(summary = "Analyze voice interview")
    public ResponseEntity<ApiResponse> analyzeVoice(
            @RequestParam Long interviewId,
            @RequestParam String voiceText,
            @RequestParam Integer duration,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        VoiceAnalysisDTO analysis = voiceAnalysisService.analyzeVoice(userId, interviewId, voiceText, duration);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Voice analyzed", analysis));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get voice analysis details")
    public ResponseEntity<ApiResponse> getVoiceAnalysis(@PathVariable Long id) {
        VoiceAnalysisDTO analysis = voiceAnalysisService.getVoiceAnalysis(id);
        return ResponseEntity.ok(new ApiResponse(true, "Analysis retrieved", analysis));
    }
    
    @GetMapping("/my-analyses")
    @Operation(summary = "Get user's voice analyses")
    public ResponseEntity<ApiResponse> getUserVoiceAnalyses(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        List<VoiceAnalysisDTO> analyses = voiceAnalysisService.getUserVoiceAnalyses(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Analyses retrieved", analyses));
    }
}
