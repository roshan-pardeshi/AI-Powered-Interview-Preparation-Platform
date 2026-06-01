package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequest {
    private String company;
    private String technology;
    private String difficulty;
    private String interviewType;
}
