package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddBoardDTO;
import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.dtos.BoardIdDTO;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.services.UserService;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
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

    @Autowired
    private BoardRepository boardRepository;

    private Board checkBoard(String boardId) {
        Board board = boardService.getBoardByBoardId(boardId);
        if (board == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found");
        }
        return board;


    }

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardById(
            @PathVariable String boardId,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        Board board = boardService.getBoardByBoardId(boardId);
        boolean isPublic = board.getVisibility().toString().equalsIgnoreCase("public");

        // Check if the token is null or empty
        if (token == null || token.isEmpty()) {
            if (!isPublic) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied to private board"));
            } else {
                BoardIdDTO boardIdDTO = modelMapper.map(board, BoardIdDTO.class);
                return ResponseEntity.ok(boardIdDTO);
            }
        }

        // Process the token if it's present
        try {
            String afterSubToken = token.substring(7);
            String oid = jwtService.getOidFromToken(afterSubToken);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            // Check if the user is not the owner of the private board
            if (!isOwner && !isPublic) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied to private board"));
            }

            BoardIdDTO boardIdDTO = modelMapper.map(board, BoardIdDTO.class);
            return ResponseEntity.ok(boardIdDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board ID not found"));
        }
    }



    @GetMapping("")
    public ResponseEntity<List<BoardIdDTO>> getAllBoard(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }

        try {
            String afterSubToken = token.substring(7);
            String oid = jwtService.getOidFromToken(afterSubToken);

            List<Board> boards = boardService.getAllBoard(oid);
            List<BoardIdDTO> boardIdDTOS = boards.stream()
                    .map(board -> modelMapper.map(board, BoardIdDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(boardIdDTOS);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve boards");
        }
    }


//    @PostMapping("")
//    public ResponseEntity<?> createBoard(
//            @RequestHeader("Authorization") String token,
//            @RequestBody(required = false) AddBoardDTO addBoardDTO) {
//
//        if (token == null) {
//            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("the access token has expired or is invalid");
//        }
//
//        if (addBoardDTO == null || addBoardDTO.getName() == null || addBoardDTO.getName().isEmpty()) {
//            throw new BadRequestException("boardName is null, empty");
//        }
//
//        if (addBoardDTO.getName().length() > MAX_LENGTH) {
//            throw new BadRequestException("boardName length > MAX-LENGTH");
//        }
//
//
//        String afterSubToken = token.substring(7);
//        String oid = jwtService.getOidFromToken(afterSubToken);
//        Board newBoard = new Board();
//        newBoard.setName(addBoardDTO.getName());
//        UserLocal owner = userService.findByOid(oid);
//        newBoard.setOwner(owner);
//        Board createdBoard = boardService.addBoard(newBoard);
//        AddBoardDTO createdBoardDTO = modelMapper.map(createdBoard, AddBoardDTO.class);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoardDTO);
//    }

    @PostMapping("")
    public ResponseEntity<?> createBoard(
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) AddBoardDTO addBoardDTO) {
        try{
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "The access token has expired or is invalid"));
            }

            if (addBoardDTO == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied, request body required"));
            }

            String afterSubToken = token.substring(7);
            String oid;
            try {
                oid = jwtService.getOidFromToken(afterSubToken);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid token"));
            }


            Board newBoard = new Board();
            newBoard.setName(addBoardDTO.getName());
            UserLocal owner = userService.findByOid(oid);
            newBoard.setOwner(owner);

            Board createdBoard = boardService.addBoard(newBoard);
            AddBoardDTO createdBoardDTO = modelMapper.map(createdBoard, AddBoardDTO.class);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdBoardDTO);
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }

    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<?> editVisibilityBoard(@RequestHeader("Authorization") String token,
                                                 @PathVariable String boardId,
                                                 @RequestBody(required = false) Map<String, String> body) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "The access token has expired or is invalid"));
        }

        String afterSubToken = token.substring(7);
        String oid;
        try {
            oid = jwtService.getOidFromToken(afterSubToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid token"));
        }
        Board board = boardService.getBoardByBoardId(boardId);
        if (body == null || !body.containsKey("visibility")){
            if (board.getOwner().getOid().equals(oid)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Visibility must be provided"));
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Visibility must be provided"));
            }
        }

        String visibility = body.get("visibility");

        if (!visibility.equalsIgnoreCase("public") && !visibility.equalsIgnoreCase("private")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Visibility should be private or public"));
        }
        board.setVisibility(BoardVisi.valueOf(visibility.toUpperCase()));
        boardRepository.save(board);

        return ResponseEntity.ok(board);
    }
}


