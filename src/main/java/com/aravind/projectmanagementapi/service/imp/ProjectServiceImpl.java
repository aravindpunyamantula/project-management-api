package com.aravind.projectmanagementapi.service.imp;

import com.aravind.projectmanagementapi.dto.project.ProjectRequest;
import com.aravind.projectmanagementapi.dto.project.ProjectResponse;
import com.aravind.projectmanagementapi.entity.Project;
import com.aravind.projectmanagementapi.entity.User;
import com.aravind.projectmanagementapi.exception.AccessDeniedException;
import com.aravind.projectmanagementapi.exception.ResourceNotFoundException;
import com.aravind.projectmanagementapi.respository.ProjectRepository;
import com.aravind.projectmanagementapi.respository.UserRepository;
import com.aravind.projectmanagementapi.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request, String userEmail) {
        User user = findUserByEmail(userEmail);

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .build();

        Project saved = projectRepository.save(project);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        return projectRepository.findByOwner(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id, String userEmail) {
        Project project = findProjectAndVerifyOwnership(id, userEmail);
        return toResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request, String userEmail) {
        Project project = findProjectAndVerifyOwnership(id, userEmail);

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        Project updated = projectRepository.save(project);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteProject(Long id, String userEmail) {
        Project project = findProjectAndVerifyOwnership(id, userEmail);
        projectRepository.delete(project);
    }

    /**
     * Finds a project by ID and verifies that the authenticated user owns it.
     * Throws 404 if not found, 403 if owned by a different user.
     */
    public Project findProjectAndVerifyOwnership(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project not found with id: " + projectId
                ));

        if (!project.getOwner().getEmail().equals(userEmail)) {
            throw new AccessDeniedException(
                    "You do not have access to this project"
            );
        }

        return project;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"
                ));
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
