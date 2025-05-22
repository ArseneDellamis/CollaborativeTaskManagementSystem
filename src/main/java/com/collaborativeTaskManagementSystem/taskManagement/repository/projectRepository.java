package com.collaborativeTaskManagementSystem.taskManagement.repository;

import com.collaborativeTaskManagementSystem.taskManagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface projectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);
}
