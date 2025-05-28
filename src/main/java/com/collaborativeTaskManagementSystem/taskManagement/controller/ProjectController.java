package com.collaborativeTaskManagementSystem.taskManagement.controller;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Status;
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
import java.util.Collections;
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
        Project createdProject = projectService.createProject(request, user);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdProject);
    }

    /**
     * Get project by id
     * @param id Project ID
     * @return Project details if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            Project project = projectService.getProjectById(id);
            return ResponseEntity.ok(project);
        } catch (RuntimeException ex) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "projectId", "Project not found with id: " + id);
        }
    }

    /**
     * Get project by name
     * @param name Project name
     * @return Project details if found
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable String name, @AuthenticationPrincipal User user) {
        try {
            Project project = projectService.getProjectByName(name);
            return ResponseEntity.ok(project);
        } catch (RuntimeException ex) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "projectName", "Project not found with name: " + name);
        }
    }

    /**
     * Update project
     * @param id Project ID
     * @param projectDto Project data to update
     * @return Updated project details
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDto projectDto) {
        
        try {

            
        } catch (RuntimeException ex) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "projectId", "Project not found with id: " + id);
        }
    }

    /**
     * Delete project
     * @param id Project ID to delete
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            // Check if project exists
            projectService.getProjectById(id);
            
            // Delete the project
            projectService.deleteProject(id);
            
            // Return 204 No Content on successful deletion
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException ex) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "projectId", "Project not found with id: " + id);
        }
    }

    /**
     * Find project by ID or name (exact match, case-insensitive)
     * @param searchTerm Project ID or name to search for
     * @return Project if found
     */
    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<?> findProjectByIdOrName(@PathVariable String searchTerm) {
        try {
            Project project = projectService.findProjectByIdOrName(searchTerm);
            return ResponseEntity.ok(project);
        } catch (RuntimeException ex) {
            return buildErrorResponse(HttpStatus.NOT_FOUND, "project", ex.getMessage());
        }
    }

    /**
     * Search for projects where ID or name contains the search term (partial match, case-insensitive)
     * @param searchTerm Term to search for in project ID or name
     * @return List of matching projects (can be empty)
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProjects(@RequestParam("q") String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "searchTerm", "Search term cannot be empty");
        }
        
        List<Project> projects = projectService.searchProjects(searchTerm);
        if (projects.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("message", "No projects found matching: " + searchTerm));
        }
        return ResponseEntity.ok(projects);
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
    
    /**
     * Build a standardized error response
     */
    private ResponseEntity<Map<String, String>> buildErrorResponse(HttpStatus status, String field, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(field, message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
