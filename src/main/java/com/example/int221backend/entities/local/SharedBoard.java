package com.example.int221backend.entities.local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "personal_board")
public class SharedBoard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shared_id")
  private Long sharedId;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserLocal owner;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;


}
