package com.example.int221backend.entities.local;

import com.example.int221backend.entities.AccessRight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "collaborators")
public class Collaborators {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String collabId;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserLocal user;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight;

    @Column(name = "added_on",insertable = false, updatable = false )
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime addedOn;

}
