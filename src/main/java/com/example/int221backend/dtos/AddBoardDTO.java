package com.example.int221backend.dtos;

import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.UserLocal;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddBoardDTO {
    private String name;
    private UserLocal owner;
    private String id;
    private String visibility;

}
