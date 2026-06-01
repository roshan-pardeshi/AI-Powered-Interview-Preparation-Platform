package com.interview.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(length = 20)
    private String phone;
    
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('USER', 'ADMIN') DEFAULT 'USER'")
    private UserRole role = UserRole.USER;
    
    @Column(columnDefinition = "LONGTEXT")
    private String skills;
    
    @Column(length = 255)
    private String experience;
    
    @Column(length = 500)
    private String profileImage;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
    
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
    
    public enum UserRole {
        USER, ADMIN
    }
}
