package com.example.int221backend.dtos;

import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskDTO {
    private String id;
    private String title;
    private String assignees;
    private Status status;
}
