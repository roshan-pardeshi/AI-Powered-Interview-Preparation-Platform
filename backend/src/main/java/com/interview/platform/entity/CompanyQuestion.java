package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String companyName;
    
    @Column(nullable = false, length = 255)
    private String technology;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM'")
    private DifficultyLevel difficulty = DifficultyLevel.MEDIUM;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String questionText;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('HR', 'TECHNICAL', 'CODING') DEFAULT 'TECHNICAL'")
    private QuestionCategory category = QuestionCategory.TECHNICAL;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
    
    public enum QuestionCategory {
        HR, TECHNICAL, CODING
    }
}
