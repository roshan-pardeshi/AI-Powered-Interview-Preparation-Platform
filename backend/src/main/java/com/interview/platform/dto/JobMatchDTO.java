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
public class JobMatchDTO {
    private Long id;
    private Long userId;
    private String jobDescription;
    private Integer matchScore;
    private String matchedSkills;
    private String missingSkills;
    private String suggestions;
    private LocalDateTime createdAt;
}
