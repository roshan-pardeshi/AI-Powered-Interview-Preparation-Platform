package com.interview.platform.repository;

import com.interview.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);
    boolean existsByEmail(String email);
}
