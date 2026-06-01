package com.interview.platform.service;

import com.interview.platform.dto.AnalyticsDTO;
import com.interview.platform.entity.Analytics;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    
    public AnalyticsDTO getUserAnalytics(Long userId) {
        Analytics analytics = analyticsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Analytics not found for user"));
        
        return mapToDTO(analytics);
    }
    
    private AnalyticsDTO mapToDTO(Analytics analytics) {
        return AnalyticsDTO.builder()
                .id(analytics.getId())
                .userId(analytics.getUser().getId())
                .totalInterviews(analytics.getTotalInterviews())
                .averageScore(analytics.getAverageScore())
                .resumeScoreAvg(analytics.getResumeScoreAvg())
                .matchScoreAvg(analytics.getMatchScoreAvg())
                .lastInterviewDate(analytics.getLastInterviewDate() != null ? 
                        analytics.getLastInterviewDate().toString() : null)
                .build();
    }
}
