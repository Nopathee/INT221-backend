package com.example.int221backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasksV2")
public class TaskV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "task_title", nullable = false, length = 100)
    private String title;

    @Column(name = "task_description", length = 500)
    private String description;

    @Column(name = "task_assignees", length = 30)
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "task_status_id", referencedColumnName = "id")
    private Status status;

    @Column(name = "created_on", updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", insertable = false , updatable = false)
    private ZonedDateTime updatedOn;

    // Constructors, getters, and setters

}
