package com.example.int221backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class ListShowCollabDTO {
    private List<ShowCollabDTO> showCollabDTOS;
}
