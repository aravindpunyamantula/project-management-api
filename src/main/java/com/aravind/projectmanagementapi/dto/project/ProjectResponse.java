package com.aravind.projectmanagementapi.dto.project;

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
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
