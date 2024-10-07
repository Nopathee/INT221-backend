package com.example.int221backend.dtos;

import com.example.int221backend.entities.local.UserLocal;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoardIdDTO {
    private String id;
    private String boardName;
    private String visibility;
    private UserLocal owner;
}
