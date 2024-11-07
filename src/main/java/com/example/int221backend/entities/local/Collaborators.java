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

    @EmbeddedId
    private BoardCollabId id;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserLocal user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_right", nullable = false)
    private AccessRight accessRight;

    @Column(name = "added_on",insertable = false, updatable = false )
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedOn;

    @PrePersist
    protected void onCreate() {
        addedOn = new Date();
    }

}
