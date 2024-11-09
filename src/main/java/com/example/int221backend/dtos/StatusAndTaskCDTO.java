package com.example.int221backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class StatusAndTaskCDTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private String boardId;
    private String taskCount;
}
