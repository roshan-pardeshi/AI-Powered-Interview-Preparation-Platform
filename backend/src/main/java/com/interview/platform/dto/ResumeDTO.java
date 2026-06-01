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
public class ResumeDTO {
    private Long id;
    private Long userId;
    private String fileName;
    private String filePath;
    private Integer resumeScore;
    private String strongSkills;
    private String weakSkills;
    private String missingSkills;
    private String suggestions;
    private LocalDateTime uploadDate;
    private LocalDateTime createdAt;
}
