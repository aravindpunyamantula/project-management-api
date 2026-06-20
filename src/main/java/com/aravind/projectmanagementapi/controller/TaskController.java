package com.aravind.projectmanagementapi.controller;

import com.aravind.projectmanagementapi.dto.task.TaskRequest;
import com.aravind.projectmanagementapi.dto.task.TaskResponse;
import com.aravind.projectmanagementapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/api/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        return taskService.createTask(projectId, request, authentication.getName());
    }

    @GetMapping("/api/projects/{projectId}/tasks")
    public List<TaskResponse> getTasksByProject(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return taskService.getTasksByProject(projectId, authentication.getName());
    }

    @GetMapping("/api/tasks/{id}")
    public TaskResponse getTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return taskService.getTaskById(id, authentication.getName());
    }

    @PutMapping("/api/tasks/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication
    ) {
        return taskService.updateTask(id, request, authentication.getName());
    }

    @DeleteMapping("/api/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        taskService.deleteTask(id, authentication.getName());
    }
}
