package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswerDTO {
    private Long id;
    private Long questionId;
    private Long interviewId;
    private String answerText;
    private String feedback;
    private Integer confidenceScore;
    private Integer communicationScore;
    private Integer technicalScore;
    private LocalDateTime createdAt;
}
