package com.example.int221backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponseDTO {
    private String access_token;
    private String refresh_token;
}
