package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;
    
    @Column
    private Integer questionNumber;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String questionText;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('HR', 'TECHNICAL', 'CODING') DEFAULT 'TECHNICAL'")
    private QuestionCategory category = QuestionCategory.TECHNICAL;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum QuestionCategory {
        HR, TECHNICAL, CODING
    }
}
