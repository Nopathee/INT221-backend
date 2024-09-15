package com.example.int221backend.dtos;


import com.example.int221backend.entities.local.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskV3DTO {
    private String id;
    private String title;
    private String assignees;
    private Status status;
}
