package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {
    private Long id;
    private Long userId;
    private Integer totalInterviews;
    private Integer averageScore;
    private Integer resumeScoreAvg;
    private Integer matchScoreAvg;
    private String lastInterviewDate;
}
