package com.aravind.projectmanagementapi.dto.task;

import com.aravind.projectmanagementapi.entity.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
