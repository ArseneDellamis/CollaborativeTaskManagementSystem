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

    //create project
    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    //get all projects
    public List<Project> getAllProjects(){
        return projectRepo.findAll();
    }

    //edit project
    public String editProject(Project project){

        return "updated";
    }

    //delete project
    public void deleteProject(Long id){
        projectRepo.deleteById(id);
    }

    //add members on the project
    public String addMembers(String name, User user){
        Project project = projectRepo
                .findByName(name)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        project.addTeamMember(user);
        return "added";
    }

    //delete a team member
    public String deleteMember(String name, User user){
        Project project = projectRepo
                .findByName(name)
                .orElseThrow(
                        ()-> new RuntimeException("Project not found")
                );
        project.removeTeamMember(user);
        return "deleted";
    }

    // get all projects by user id
    public List<Project> getProjectsByUser(User user){
        return projectRepo.findAllByProjectManagerId(user.getId());
    }

    //get all project as a team member
    public List<Project> getProjectsByTeamMember(User user){
        return projectRepo.findAllByTeamMembersContains(user);
    }


}
