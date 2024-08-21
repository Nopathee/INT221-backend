package com.example.int221backend.user_dtos;

public class AuthResponseDTO {
    private String token;
    private String fullname;

    public AuthResponseDTO(String token, String fullname) {
        this.token = token;
        this.fullname = fullname;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
