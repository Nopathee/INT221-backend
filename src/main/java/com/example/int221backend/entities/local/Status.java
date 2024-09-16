package com.example.int221backend.entities.local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status_name", nullable = false, length = 50)
    private String name;

    @Column(name = "status_description", length = 200)
    private String description;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "created_on", updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private ZonedDateTime updatedOn;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
}
