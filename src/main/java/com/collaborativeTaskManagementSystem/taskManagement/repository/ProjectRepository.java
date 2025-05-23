package com.collaborativeTaskManagementSystem.taskManagement.repository;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);
    //get all projects by user id
    List<Project> findAllByProjectManagerId(Long id);
    // get all project as a member
    List<Project> findAllByTeamMembersContains(User user);

    boolean existsByNameIgnoreCase(@NotBlank(message = "Project name is required")
                                   @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters") String projectName);
}
