package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepo;

    /**
     *
     * CREATE PROJECT
     *
     */
    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    /**
     *
     * GET ALL PROJECTS
     */
    public List<Project> getAllProjects(){
        return projectRepo.findAll();
    }

    /**
     * EDIT PROJECT
     */
    public Project editProject(Project project){

        return projectRepo.save(project);

    }

    /**
     *
     * DELETE PROJECT
     */
    public void deleteProject(Long id){
        projectRepo.deleteById(id);
    }

    /**
     *
     * ADD MEMBERS TO PROJECT
     */
    public String addMembers(String name, User user){
        Project project = projectRepo
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
        Project project = projectRepo
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
        return projectRepo.findAllByProjectManagerId(user.getId());
    }

    /**
     *
     * GET PROJECTS BY TEAM MEMBER
     */
    public List<Project> getProjectsByTeamMember(User user){
        return projectRepo.findAllByTeamMembersContains(user);
    }

    /**
     *
     * GET PROJECT BY ID
     */
    public Project getProjectById(Long id) {
        return projectRepo
                .findById(id)
                .orElseThrow(()-> new RuntimeException("Project not found"));
    }

    /**
     *
     * GET PROJECT BY NAME
     */
    public Project getProjectByName(String name) {

        return projectRepo
                .findByName(name)
                .orElseThrow(()-> new RuntimeException("Project not found"));
    }
}
