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
public class InterviewQuestionDTO {
    private Long id;
    private Long interviewId;
    private Integer questionNumber;
    private String questionText;
    private String category;
    private LocalDateTime createdAt;
}
