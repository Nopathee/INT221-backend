package com.example.int221backend.entities.local;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BoardCollabId implements Serializable {
    private String boardId;
    private String userOid;
}
