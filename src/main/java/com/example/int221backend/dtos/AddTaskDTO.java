package com.example.int221backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddTaskDTO {
    private String id;
    private String title;
    private String description;
    private String assignees;
    private String status;
}
