package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 255)
    private String fileName;
    
    @Column(nullable = false, length = 500)
    private String filePath;
    
    @Column
    private Integer resumeScore;
    
    @Column(columnDefinition = "JSON")
    private String strongSkills;
    
    @Column(columnDefinition = "JSON")
    private String weakSkills;
    
    @Column(columnDefinition = "JSON")
    private String missingSkills;
    
    @Column(columnDefinition = "JSON", length = 2000)
    private String suggestions;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
