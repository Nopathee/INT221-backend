package com.example.int221backend.dtos;

import com.example.int221backend.entities.BoardVisi;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddBoardDTO {
    private String name;
    private String id;
    private String visibility;

}
