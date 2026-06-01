package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;
    
    @Column
    private Integer confidenceScore;
    
    @Column
    private Integer communicationScore;
    
    @Column
    private Integer technicalScore;
    
    @Column(columnDefinition = "JSON")
    private String fillerWords;
    
    @Column(length = 50)
    private String speakingSpeed;
    
    @Column(columnDefinition = "JSON")
    private String repeatedWords;
    
    @Column(columnDefinition = "JSON", length = 2000)
    private String suggestions;
    
    @Column
    private Integer audioDurationSeconds;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
