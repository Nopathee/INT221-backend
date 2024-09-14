package com.example.int221backend.controllers;

import com.example.int221backend.dtos.BoardIdDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<List<BoardIdDTO>> getAllBoard(@RequestHeader ("Authorization") String token) {
        String afterSubToken = token.substring(7);
        String oid = jwtService.getOidFromToken(afterSubToken);
        System.out.println(oid);
        List<Board> boards = boardService.getAllBoard(oid);
        List<BoardIdDTO> boardIdDTOS = boards.stream()
                .map(board -> modelMapper.map(board, BoardIdDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(boardIdDTOS);
    }

}
