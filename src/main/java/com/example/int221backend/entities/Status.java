package com.example.int221backend.entities;

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

    @Column(name = "statusName" , nullable = false , length = 50)
    private String name;

    @Column(name = "statusDescription" ,length = 200)
    private String description;

    @Column(name = "created_on", updatable = false, insertable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", insertable = false ,updatable = false)
    private ZonedDateTime updatedOn;
}
