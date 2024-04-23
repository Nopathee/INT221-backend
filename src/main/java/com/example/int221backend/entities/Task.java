package com.example.int221backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "task_id")
    private Integer taskId;

    @Column(name = "task_title", nullable = false, length = 100)
    private String taskTitle;

    @Column(name = "task_description", length = 500)
    private String taskDescription;

    @Column(name = "task_assignees", length = 30)
    private String taskAssignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus;

    @Column(name = "created_on", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    // Constructors, getters, and setters

}
