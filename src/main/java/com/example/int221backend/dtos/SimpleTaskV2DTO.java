package com.example.int221backend.dtos;


import com.example.int221backend.entities.local.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskV2DTO {
    private String id;
    private String title;
    private String assignees;
    private Status status;
}
