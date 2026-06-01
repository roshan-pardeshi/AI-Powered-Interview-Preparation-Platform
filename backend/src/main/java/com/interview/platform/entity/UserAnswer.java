package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @Column(columnDefinition = "LONGTEXT")
    private String answerText;
    
    @Column(columnDefinition = "LONGTEXT")
    private String feedback;
    
    @Column
    private Integer confidenceScore;
    
    @Column
    private Integer communicationScore;
    
    @Column
    private Integer technicalScore;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
