package com.example.int221backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardResponseDTO {
    private List<BoardIdDTO> personalBoard;
    private List<BoardIdDTO> collaboratorBoard;

    public BoardResponseDTO(List<BoardIdDTO> personalBoard, List<BoardIdDTO> collaboratorBoard) {
        this.personalBoard = personalBoard;
        this.collaboratorBoard = collaboratorBoard;
    }
}