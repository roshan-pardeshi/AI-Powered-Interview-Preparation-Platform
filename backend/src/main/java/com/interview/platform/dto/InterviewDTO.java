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
public class InterviewDTO {
    private Long id;
    private Long userId;
    private String company;
    private String technology;
    private String difficulty;
    private String interviewType;
    private Integer score;
    private String feedback;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
}
