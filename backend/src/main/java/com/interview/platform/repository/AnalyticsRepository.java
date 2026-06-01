package com.interview.platform.repository;

import com.interview.platform.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Optional<Analytics> findByUserId(Long userId);
}
