package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Builder.Default
    @Column
    private Integer totalInterviews = 0;
    
    @Builder.Default
    @Column
    private Integer averageScore = 0;
    
    @Builder.Default
    @Column
    private Integer resumeScoreAvg = 0;
    
    @Builder.Default
    @Column
    private Integer matchScoreAvg = 0;
    
    @Column
    private LocalDateTime lastInterviewDate;
    
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
