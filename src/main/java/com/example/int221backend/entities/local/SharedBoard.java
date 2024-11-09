package com.example.int221backend.entities.local;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "personal_boards")
public class SharedBoard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sharedId;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false)
  private UserLocal owner;

  @ManyToOne
  @JoinColumn(name = "board_id", nullable = false)
  private Board board;

//    @Column(name = "added_on")
//    private ZonedDateTime addedOn;
//
//    @NotNull
//    @Enumerated(EnumType.STRING)
//    @Column(name = "access_right", nullable = false)
//    private AccessRight accessRight = AccessRight.READ;
//
//    public enum AccessRight {
//        READ,
//        WRITE
//    }


}
