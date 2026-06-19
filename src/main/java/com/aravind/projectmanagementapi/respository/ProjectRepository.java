package com.aravind.projectmanagementapi.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.aravind.projectmanagementapi.entity.Project;
import java.util.List;
import com.aravind.projectmanagementapi.entity.User;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwner(User owner);
}
