package com.collaborativeTaskManagementSystem.taskManagement.repository;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);
    
    /**
     * Find a project by ID or exact name match (case-insensitive)
     * @param searchTerm Can be either project ID or project name
     * @return Optional containing the project if found
     */
    @Query("SELECT p FROM Project p WHERE " +
           "(CAST(:searchTerm AS string) IS NOT NULL AND CAST(:searchTerm AS string) = CAST(p.id AS string)) OR " +
           "LOWER(p.name) = LOWER(:searchTerm)")
    Optional<Project> findByIdOrNameIgnoreCase(@Param("searchTerm") String searchTerm);
    
    /**
     * Search projects by ID containing or name containing (case-insensitive partial match)
     * @param searchTerm Search term to look for in ID or name
     * @return List of matching projects
     */
    @Query("SELECT p FROM Project p WHERE CAST(p.id AS string) LIKE %:searchTerm% OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Project> searchByIdOrNameContainingIgnoreCase(@Param("searchTerm") String searchTerm);
    
    // Get all projects by user id (project manager)
    List<Project> findAllByProjectManagerId(Long id);
    
    // Get all projects where user is a team member
    List<Project> findAllByTeamMembersContains(User user);

    boolean existsByNameIgnoreCase(@NotBlank(message = "Project name is required")
                                 @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters") String projectName);
    
    /**
     * Check if a project with the given name exists (case-insensitive) excluding a specific project
     * Useful for update operations to avoid name conflicts
     * @param name Project name to check
     * @param id Project ID to exclude from the check
     * @return true if a project with the name exists (excluding the specified ID)
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Project p WHERE LOWER(p.name) = LOWER(:name) AND p.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);
}
