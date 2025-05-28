package com.collaborativeTaskManagementSystem.taskManagement.service;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import com.collaborativeTaskManagementSystem.taskManagement.model.User;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.Status;
import com.collaborativeTaskManagementSystem.taskManagement.model.constants.TeamPermissions;
import com.collaborativeTaskManagementSystem.taskManagement.payload.ProjectDto;
import com.collaborativeTaskManagementSystem.taskManagement.repository.ProjectRepository;
import com.collaborativeTaskManagementSystem.taskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final UserRepository UserRepository;


}
