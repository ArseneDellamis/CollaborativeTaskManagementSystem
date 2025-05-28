package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Status;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.TeamPermissions;
import com.collaborativeTaskManagementSystem.taskManagement.payload.ProjectDto;
import com.collaborativeTaskManagementSystem.taskManagement.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;

    /**
     *
     * GET ALL PROJECTS
     */
    public List<Project> getAllProjects(){
        return repository.findAll();
    }
    /**
     *
     * CREATE PROJECT
     *
     */
    public Project createProject(ProjectDto request, User user){
        Project project = Project.builder()
                .name(request.getProjectName())
                .projectManager(user)
                .description(request.getProjectDescription())
                .status(Status.valueOf(request.getStatus().toUpperCase()))
                .permission(TeamPermissions.OWN_TASKS)
                .startDate(LocalDate.now())
                .build();
        return repository.save(project);
    }


    /**
     * UPDATE PROJECT
     */
    public void updateProject(Long id, ProjectDto request){
        // Check if project exists
        Project project = repository
                .findById(id)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        String permission = project.getPermission().name();
        // Check if user has permission to update project
        if (permission.equalsIgnoreCase(TeamPermissions.OWN_TASKS.name())) {
            project.setName(request.getProjectName());
            project.setDescription(request.getProjectDescription());
            project.setStatus(Status.valueOf(request.getStatus().toUpperCase()));
        }
        repository.save(project);
    }

    /**
     *
     * DELETE PROJECT
     */
    public void deleteProject(Long id){
        Project project = repository
                .findById(id)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        String permission = project.getPermission().name();
        // Check if user has permission to delete project
        if (!permission.equalsIgnoreCase(TeamPermissions.OWN_TASKS.name())) {
            throw new RuntimeException("You do not have permission to delete this project");
        }
        repository.deleteById(id);
    }

    /**
     *
     * ADD MEMBERS TO PROJECT
     */
    public String addMembers(String name, User user){
        Project project = repository
                .findByName(name)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        project.addTeamMember(user);
        return "added";
    }

    /**
     *
     * DELETE MEMBERS FROM PROJECT
     */
    public String deleteMember(String name, User user){
        Project project = repository
                .findByName(name)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        project.removeTeamMember(user);
        return "deleted";
    }

    /**
     *
     * GET PROJECTS BY USER
     */
    public List<Project> getProjectsByUser(User user){
        return repository.findAllByProjectManagerId(user.getId());
    }

    /**
     *
     * GET PROJECTS BY TEAM MEMBER
     */
    public List<Project> getProjectsByTeamMember(User user){
        return repository.findAllByTeamMembersContains(user);
    }

    /**
     *
     * GET PROJECT BY ID
     */
    public Project getProjectById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(()-> new RuntimeException("Project not found"));
    }

    /**
     *
     * GET PROJECT BY NAME
     */
    public Project getProjectByName(String name) {
        return repository
                .findByName(name)
                .orElseThrow(()-> new RuntimeException("Project not found with name: " + name));
    }
    
    /**
     * Find a project by either ID or name (case-insensitive exact match)
     * @param searchTerm Can be project ID or project name
     * @return The found project
     * @throws RuntimeException if no project is found
     */
    public Project findProjectByIdOrName(String searchTerm) {
        return repository.findByIdOrNameIgnoreCase(searchTerm)
                .orElseThrow(() -> new RuntimeException("Project not found with ID or name: " + searchTerm));
    }
    
    /**
     * Search for projects where ID or name contains the search term (case-insensitive partial match)
     * @param searchTerm The term to search for in project ID or name
     * @return List of matching projects (empty if none found)
     */
    public List<Project> searchProjects(String searchTerm) {
        return repository.searchByIdOrNameContainingIgnoreCase(searchTerm);
    }
}
