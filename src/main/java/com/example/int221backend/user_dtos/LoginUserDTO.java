package com.example.int221backend.user_dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserDTO {
    @NotNull
    @NotBlank
    @Size(max = 50, message = "username or password is incorrect")
    private String userName;

    @NotNull
    @NotBlank
    @Size(max = 14, message = "username or password is incorrect")
    private String password;
}
