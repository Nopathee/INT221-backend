package com.example.int221backend.controllers;

import com.example.int221backend.dtos.BoardIdDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.services.UserService;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards")
public class BoardController {
    private static final int MAX_LENGTH = 120;
    @Autowired
    private BoardService boardService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

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

    @PostMapping("")
    public ResponseEntity<?> createBoard(
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) BoardIdDTO boardIdDTO) {


        if (token == null) {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("the access token has expired or is invalid");
        }

        if (boardIdDTO == null || boardIdDTO.getName() == null || boardIdDTO.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("boardName is null, empty, or length > MAX-LENGTH");
        }

        if (boardIdDTO.getName().length() > MAX_LENGTH) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("boardName is null, empty, or length > MAX-LENGTH");
        }


        String afterSubToken = token.substring(7);
        String oid = jwtService.getOidFromToken(afterSubToken);

        Board newBoard = new Board();
        newBoard.setName(boardIdDTO.getName());
        UserLocal owner = userService.findByOid(oid);
        newBoard.setOwner(owner);
        Board createdBoard = boardService.addBoard(newBoard);
        BoardIdDTO createdBoardDTO = modelMapper.map(createdBoard, BoardIdDTO.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoardDTO);
    }



}


