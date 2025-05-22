package com.collaborativeTaskManagementSystem.taskManagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "priorities")
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // e.g., "Low", "Medium", "High", "Urgent"

    @Column(nullable = false)
    private Integer level;  // e.g., 1, 2, 3, 4 (used for sorting)

    @Column(nullable = false)
    private String color;  // e.g., "#FF0000" for red

    @Column(nullable = false)
    private Boolean active = true;  // Can be deactivated if not needed

    @OneToMany(mappedBy = "priority")
    private List<Task> tasks;
}