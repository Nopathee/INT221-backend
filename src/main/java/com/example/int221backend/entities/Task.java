package com.example.int221backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "task_title", nullable = false, length = 100)
    private String title;

    @Column(name = "task_description", length = 500)
    private String description;

    @Column(name = "task_assignees", length = 30)
    private String assignees;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatus status;

    @Column(name = "created_on", nullable = false, updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", nullable = false, insertable = false )
    private ZonedDateTime updatedOn;

    // Constructors, getters, and setters

}
