package com.example.int221backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserLocalResponse {
    private String userId;
    private String username;
    private String name;
    private String email;
}
