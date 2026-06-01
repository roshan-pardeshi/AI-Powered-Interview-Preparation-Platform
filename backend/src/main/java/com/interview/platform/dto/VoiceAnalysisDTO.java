package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceAnalysisDTO {
    private Long id;
    private Long userId;
    private Long interviewId;
    private Integer confidenceScore;
    private Integer communicationScore;
    private Integer technicalScore;
    private String fillerWords;
    private String speakingSpeed;
    private String repeatedWords;
    private String suggestions;
    private Integer audioDurationSeconds;
}
