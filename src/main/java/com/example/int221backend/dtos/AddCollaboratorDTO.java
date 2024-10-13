package com.example.int221backend.dtos;

import com.example.int221backend.entities.AccessRight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCollaboratorDTO {
    private String email;
    private AccessRight access_right;
}
