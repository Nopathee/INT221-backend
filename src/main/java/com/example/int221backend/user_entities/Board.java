package com.example.int221backend.user_entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", length = 10)
    private String boardId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
