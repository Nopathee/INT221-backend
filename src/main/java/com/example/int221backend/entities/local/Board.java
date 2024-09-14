package com.example.int221backend.entities.local;

import com.example.int221backend.entities.shared.User;
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
    @Column(name = "board_id", nullable = false, length = 36)
    private String boardId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserLocal owner;

    @Column(name = "board_name", nullable = false, length = 100)
    private String name;



}