package com.example.int221backend.entities.local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "tasksV3")
public class TaskV3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "task_title", nullable = false, length = 100)
    private String title;

    @Column(name = "task_description", length = 500)
    private String description;

    @Column(name = "task_assignees", columnDefinition = "TINYTEXT")
    private String assignees;

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    private Status status;

    @Column(name = "created_on", updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
