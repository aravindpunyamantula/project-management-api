package com.aravind.projectmanagementapi.respository;

import com.aravind.projectmanagementapi.entity.Project;
import com.aravind.projectmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository
        extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);
}