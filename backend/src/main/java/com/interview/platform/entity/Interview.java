package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 255)
    private String company;
    
    @Column(nullable = false, length = 255)
    private String technology;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM'")
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('TEXT', 'VOICE') DEFAULT 'TEXT'")
    private InterviewType interviewType = InterviewType.TEXT;
    
    @Column
    private Integer score;
    
    @Column(columnDefinition = "JSON", length = 2000)
    private String feedback;
    
    @Column
    private Integer durationSeconds;
    
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
    
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
    
    public enum InterviewType {
        TEXT, VOICE
    }
}
