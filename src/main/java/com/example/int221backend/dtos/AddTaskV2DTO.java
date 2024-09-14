package com.example.int221backend.dtos;

import com.example.int221backend.entities.local.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddTaskV2DTO {
    private String id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
}