package com.example.int221backend.entities.local;

import com.example.int221backend.entities.AccessRight;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "collaborators")
public class Collaborators {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collab_id")
    private Integer collabId;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserLocal user;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight;

    @Column(name = "added_on" )
    private ZonedDateTime addedOn;

}
