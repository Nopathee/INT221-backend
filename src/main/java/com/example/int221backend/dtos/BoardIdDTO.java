package com.example.int221backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoardIdDTO {
    private String boardId;

    private String nameBoard;
    public String getName() {
        return nameBoard;
    }
}
