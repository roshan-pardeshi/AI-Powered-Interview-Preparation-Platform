package com.interview.platform.repository;

import com.interview.platform.entity.VoiceAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VoiceAnalysisRepository extends JpaRepository<VoiceAnalysis, Long> {
    List<VoiceAnalysis> findByUserId(Long userId);
    VoiceAnalysis findByInterviewId(Long interviewId);
}
