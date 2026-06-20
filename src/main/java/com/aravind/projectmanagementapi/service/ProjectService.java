package com.aravind.projectmanagementapi.service;

import com.aravind.projectmanagementapi.dto.project.ProjectRequest;
import com.aravind.projectmanagementapi.dto.project.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(ProjectRequest request, String userEmail);

    List<ProjectResponse> getProjectsByUser(String userEmail);

    ProjectResponse getProjectById(Long id, String userEmail);

    ProjectResponse updateProject(Long id, ProjectRequest request, String userEmail);

    void deleteProject(Long id, String userEmail);
}
