package com.example.int221backend.dtos;

import com.example.int221backend.entities.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskDTO {
    private Integer taskId;
    private String taskTitle;
    private String assignees;
    private String status;
}
