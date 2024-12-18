package com.example.int221backend.entities.local;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usersLocal")
public class UserLocal {
    @Id
    @Column(name = "owner_id", nullable = false, length = 36)
    private String oid;

    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @NotNull
    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @NotNull
    @ColumnDefault("'STUDENT'")
    @Lob
    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "created_on", nullable = false)
    private ZonedDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private ZonedDateTime updatedOn;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private List<SharedBoard> sharedBoards;
}