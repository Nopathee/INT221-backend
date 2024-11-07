package com.example.int221backend.entities.local;

import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.shared.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import io.viascom.nanoid.NanoId;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {
    @Id
    @Column(name = "board_id",unique = true , nullable = false, length = 10)
    private String boardId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserLocal owner;
    
    @Column(name = "board_name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Status> statuses;

    @OneToMany(mappedBy = "board")
    private List<TaskV3> tasks;

    @OneToMany(mappedBy = "board")
    private List<Collaborators> collaborators;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private BoardVisi visibility = BoardVisi.PRIVATE;

//    @Column(name = "createdOn", updatable = false, insertable = false)
//    private Date createdOn;
//
//    @Column(name = "updatedOn", updatable = false, insertable = false)
//    private Date updatedOn;

    @PrePersist
    private void prePersist() {
        if (this.boardId == null) {
            this.boardId = NanoId.generate(10);
        }
        if (this.visibility == null) {
            this.visibility = BoardVisi.PRIVATE;
        }
    }


}
