package com.aravind.projectmanagementapi.controller;

import com.aravind.projectmanagementapi.dto.project.ProjectRequest;
import com.aravind.projectmanagementapi.dto.project.ProjectResponse;
import com.aravind.projectmanagementapi.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        return projectService.createProject(request, authentication.getName());
    }

    @GetMapping
    public List<ProjectResponse> getProjects(Authentication authentication) {
        return projectService.getProjectsByUser(authentication.getName());
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return projectService.getProjectById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication
    ) {
        return projectService.updateProject(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @PathVariable Long id,
            Authentication authentication
    ) {
        projectService.deleteProject(id, authentication.getName());
    }
}
