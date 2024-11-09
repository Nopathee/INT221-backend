package com.example.int221backend.dtos;

import com.example.int221backend.entities.AccessRight;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddCollabDTO  {
    @NotEmpty
    @Size(max = 50)
    private String email;
    private String accessRight;
}
