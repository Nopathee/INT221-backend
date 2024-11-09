package com.example.int221backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoardUpdateDTO {
    private String boardId;
    private String message;
    private String visibility;
}
