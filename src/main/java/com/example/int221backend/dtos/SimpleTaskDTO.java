package com.example.int221backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskDTO {
    private String id;
    private String title;
    private String assignees;
    private String status;
}
