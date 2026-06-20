package com.aravind.projectmanagementapi.service;

import com.aravind.projectmanagementapi.dto.task.TaskRequest;
import com.aravind.projectmanagementapi.dto.task.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(Long projectId, TaskRequest request, String userEmail);

    List<TaskResponse> getTasksByProject(Long projectId, String userEmail);

    TaskResponse getTaskById(Long taskId, String userEmail);

    TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail);

    void deleteTask(Long taskId, String userEmail);
}
