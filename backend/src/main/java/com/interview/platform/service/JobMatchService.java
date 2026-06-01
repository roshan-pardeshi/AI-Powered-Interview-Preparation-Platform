package com.interview.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.JobMatchDTO;
import com.interview.platform.dto.JobMatchRequest;
import com.interview.platform.entity.JobMatch;
import com.interview.platform.entity.Resume;
import com.interview.platform.entity.User;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.AnalyticsRepository;
import com.interview.platform.repository.JobMatchRepository;
import com.interview.platform.repository.ResumeRepository;
import com.interview.platform.repository.UserRepository;
import com.interview.platform.util.PdfExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class JobMatchService {
    
    private final JobMatchRepository jobMatchRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AnalyticsRepository analyticsRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public JobMatchDTO matchJobWithResume(Long userId, JobMatchRequest request) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        
        if (!resume.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Resume not found for this user");
        }
        
        // Extract resume text
        String resumeText = PdfExtractor.extractTextFromPdf(resume.getFilePath());
        
        // Get analysis from Gemini
        String analysisResult = geminiService.analyzeJobMatch(resumeText, request.getJobDescription());
        
        // Parse analysis result
        Map<String, Object> analysis = parseGeminiResponse(analysisResult);
        
        // Save job match
        JobMatch jobMatch = JobMatch.builder()
                .user(user)
                .jobDescription(request.getJobDescription())
                .matchScore(toInt(analysis.getOrDefault("matchScore", 0)))
                .matchedSkills(objectMapper.writeValueAsString(analysis.getOrDefault("matchedSkills", new ArrayList<>())))
                .missingSkills(objectMapper.writeValueAsString(analysis.getOrDefault("missingSkills", new ArrayList<>())))
                .suggestions(objectMapper.writeValueAsString(analysis.getOrDefault("suggestions", new ArrayList<>())))
                .build();
        
        jobMatch = jobMatchRepository.save(jobMatch);
        
        // Update analytics
        updateMatchAnalytics(userId);
        
        return mapToDTO(jobMatch);
    }
    
    public List<JobMatchDTO> getUserJobMatches(Long userId) {
        return jobMatchRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public JobMatchDTO getJobMatch(Long jobMatchId) {
        JobMatch jobMatch = jobMatchRepository.findById(jobMatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Job match not found"));
        return mapToDTO(jobMatch);
    }
    
    @Transactional
    public void deleteJobMatch(Long jobMatchId) {
        jobMatchRepository.deleteById(jobMatchId);
    }
    
    private void updateMatchAnalytics(Long userId) {
        var analyticsOpt = analyticsRepository.findByUserId(userId);
        if (analyticsOpt.isPresent()) {
            var analytics = analyticsOpt.get();
            List<JobMatch> matches = jobMatchRepository.findByUserId(userId);
            int averageScore = matches.isEmpty() ? 0 :
                    (int) Math.round(matches.stream()
                            .mapToInt(JobMatch::getMatchScore)
                            .average()
                            .orElse(0));
            analytics.setMatchScoreAvg(averageScore);
            analyticsRepository.save(analytics);
        }
    }
    
    private Map<String, Object> parseGeminiResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            result.put("matchScore", 0);
            result.put("matchedSkills", new ArrayList<>());
            result.put("missingSkills", new ArrayList<>());
            result.put("suggestions", new ArrayList<>());
        }
        return result;
    }
    
    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private JobMatchDTO mapToDTO(JobMatch jobMatch) {
        return JobMatchDTO.builder()
                .id(jobMatch.getId())
                .userId(jobMatch.getUser().getId())
                .jobDescription(jobMatch.getJobDescription())
                .matchScore(jobMatch.getMatchScore())
                .matchedSkills(jobMatch.getMatchedSkills())
                .missingSkills(jobMatch.getMissingSkills())
                .suggestions(jobMatch.getSuggestions())
                .createdAt(jobMatch.getCreatedAt())
                .build();
    }
}
