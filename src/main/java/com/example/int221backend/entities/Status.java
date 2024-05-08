package com.example.int221backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "statusName" , nullable = false , length = 50)
    private String name;

    @Column(name = "statusDescription", nullable = false ,length = 200)
    private String description;
}
