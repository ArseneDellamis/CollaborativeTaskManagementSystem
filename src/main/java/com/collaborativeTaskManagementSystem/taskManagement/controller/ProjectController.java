package com.collaborativeTaskManagementSystem.taskManagement.controller;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.Status;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.payload.ProjectDto;
import com.collaborativeTaskManagementSystem.taskManagement.repository.ProjectRepository;
import com.collaborativeTaskManagementSystem.taskManagement.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    
    private final ProjectService projectService;
    private final ProjectRepository repository;

    /**
     * Get all projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }


    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<?> createProject(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProjectDto request) {
            
        // Check if project with the same name already exists
        if (repository.existsByNameIgnoreCase(request.getProjectName())) {
            return buildErrorResponse(
                HttpStatus.CONFLICT, 
                "projectName", 
                "A project with this name already exists");
        }

        // Validate status
        if (!request.isValidStatus()) {
            return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "status",
                "Invalid status. Valid statuses are: " + ProjectDto.getValidStatusValues());
        }

        // Create and save project
        Project project = Project.builder()
            .name(request.getProjectName())
            .projectManager(user)
            .description(request.getProjectDescription())
            .status(Status.valueOf(request.getStatus().toUpperCase()))
            .startDate(LocalDate.now())
            .build();

        Project createdProject = projectService.createProject(project);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdProject);
    }
    
    /**
     * Handle validation errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    
    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String field, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(field, message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
