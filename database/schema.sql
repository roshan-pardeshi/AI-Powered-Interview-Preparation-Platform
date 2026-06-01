-- AI Interview Preparation Platform Database Schema
-- MySQL Database

CREATE DATABASE IF NOT EXISTS interview_platform;
USE interview_platform;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    skills TEXT,
    experience VARCHAR(255),
    profile_image VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_email (email)
);

-- Resumes Table
CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    resume_score INT,
    strong_skills JSON,
    weak_skills JSON,
    missing_skills JSON,
    suggestions JSON,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Job Matches Table
CREATE TABLE IF NOT EXISTS job_matches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    job_description TEXT NOT NULL,
    match_score INT,
    matched_skills JSON,
    missing_skills JSON,
    suggestions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Mock Interviews Table
CREATE TABLE IF NOT EXISTS interviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    company VARCHAR(255) NOT NULL,
    technology VARCHAR(255) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    interview_type ENUM('TEXT', 'VOICE') DEFAULT 'TEXT',
    score INT,
    feedback JSON,
    duration_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_company (company)
);

-- Interview Questions Table
CREATE TABLE IF NOT EXISTS interview_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    question_number INT,
    question_text TEXT NOT NULL,
    category ENUM('HR', 'TECHNICAL', 'CODING') DEFAULT 'TECHNICAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    INDEX idx_interview_id (interview_id)
);

-- User Answers Table
CREATE TABLE IF NOT EXISTS user_answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    interview_id BIGINT NOT NULL,
    answer_text TEXT,
    feedback TEXT,
    confidence_score INT,
    communication_score INT,
    technical_score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES interview_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    INDEX idx_interview_id (interview_id)
);

-- Voice Analysis Table
CREATE TABLE IF NOT EXISTS voice_analysis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    interview_id BIGINT,
    confidence_score INT,
    communication_score INT,
    technical_score INT,
    filler_words JSON,
    speaking_speed VARCHAR(50),
    repeated_words JSON,
    suggestions JSON,
    audio_duration_seconds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Chat Messages Table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    interview_id BIGINT NOT NULL,
    sender_type ENUM('USER', 'AI') DEFAULT 'AI',
    message TEXT NOT NULL,
    message_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    INDEX idx_interview_id (interview_id),
    INDEX idx_created_at (created_at)
);

-- Company Questions Table
CREATE TABLE IF NOT EXISTS company_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(255) NOT NULL,
    technology VARCHAR(255) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    question_text TEXT NOT NULL,
    category ENUM('HR', 'TECHNICAL', 'CODING') DEFAULT 'TECHNICAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_company_tech (company_name, technology)
);

-- User Session Table
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token)
);

-- Analytics Table
CREATE TABLE IF NOT EXISTS analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_interviews INT DEFAULT 0,
    average_score INT DEFAULT 0,
    resume_score_avg INT DEFAULT 0,
    match_score_avg INT DEFAULT 0,
    last_interview_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_id (user_id)
);

-- Admin Logs Table
CREATE TABLE IF NOT EXISTS admin_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    description TEXT,
    target_user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_admin_id (admin_id)
);

-- Create Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_resumes_upload_date ON resumes(upload_date);
CREATE INDEX idx_interviews_date ON interviews(created_at);
CREATE INDEX idx_matches_date ON job_matches(created_at);
