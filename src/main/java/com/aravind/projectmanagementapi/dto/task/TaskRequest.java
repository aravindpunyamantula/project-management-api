package com.aravind.projectmanagementapi.dto.task;

import com.aravind.projectmanagementapi.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    @NotNull(message = "Task status is required")
    private TaskStatus status;
}
