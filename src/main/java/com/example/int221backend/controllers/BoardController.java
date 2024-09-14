package com.example.int221backend.controllers;

import com.example.int221backend.entities.Board;
import com.example.int221backend.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("")
    public ResponseEntity<List<Board>> getAllBoard() {
        List<Board> boards = boardService.getAllBoard();
        return ResponseEntity.ok(boards);
    }

}
