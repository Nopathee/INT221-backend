package com.example.int221backend.entities.local;

import com.example.int221backend.entities.shared.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;

@Getter
@Setter
@Entity
@Table(name = "board")
public class Board {
    @Id
    @Column(name = "board_id", nullable = false, length = 10)
    private String boardId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserLocal owner;

    @Column(name = "board_name", nullable = false, length = 100)
    private String name;

    @PrePersist
    protected void onCreate() {
        this.boardId = generateBoardId();
    }

    private String generateBoardId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(36);
            char randomChar = (char) (randomIndex < 10 ? '0' + randomIndex : 'A' + randomIndex - 10);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
