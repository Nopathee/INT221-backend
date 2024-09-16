package com.example.int221backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddStatusV3DTO {
    private Integer id;
    private String name;
    private String description;
    private String color;
    private String bId;
}