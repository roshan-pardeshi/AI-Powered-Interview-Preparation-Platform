package com.interview.platform.repository;

import com.interview.platform.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUserId(Long userId);
    List<Interview> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Interview> findByCompany(String company);
    List<Interview> findByCompanyAndTechnology(String company, String technology);
}
