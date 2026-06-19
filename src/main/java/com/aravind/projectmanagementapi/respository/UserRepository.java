package com.aravind.projectmanagementapi.respository;

import java.util.Optional;
import com.aravind.projectmanagementapi.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
} 
