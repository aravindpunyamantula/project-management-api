package com.aravind.projectmanagementapi.service.imp;

import com.aravind.projectmanagementapi.dto.task.TaskRequest;
import com.aravind.projectmanagementapi.dto.task.TaskResponse;
import com.aravind.projectmanagementapi.entity.Project;
import com.aravind.projectmanagementapi.entity.Task;
import com.aravind.projectmanagementapi.exception.AccessDeniedException;
import com.aravind.projectmanagementapi.exception.ResourceNotFoundException;
import com.aravind.projectmanagementapi.respository.TaskRepository;
import com.aravind.projectmanagementapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectServiceImpl projectService;

    @Override
    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, String userEmail) {
        Project project = projectService.findProjectAndVerifyOwnership(projectId, userEmail);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .project(project)
                .build();

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(Long projectId, String userEmail) {
        Project project = projectService.findProjectAndVerifyOwnership(projectId, userEmail);

        return taskRepository.findByProject(project)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, String userEmail) {
        Task task = findTaskAndVerifyOwnership(taskId, userEmail);
        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, String userEmail) {
        Task task = findTaskAndVerifyOwnership(taskId, userEmail);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());

        Task updated = taskRepository.save(task);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, String userEmail) {
        Task task = findTaskAndVerifyOwnership(taskId, userEmail);
        taskRepository.delete(task);
    }

    /**
     * Finds a task by ID, then looks up its parent project and verifies
     * the authenticated user owns that parent project.
     */
    private Task findTaskAndVerifyOwnership(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId
                ));

        if (!task.getProject().getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException(
                    "You do not have access to this task"
            );
        }

        return task;
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
