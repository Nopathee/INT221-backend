package com.example.int221backend.controllers;

import com.example.int221backend.dtos.*;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Collaborators;
import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.AccessDeniedException;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.services.CollabService;
import com.example.int221backend.services.UserService;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th","https://intproj23.sit.kmutt.ac.th"})
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
    private CollabService collabService;

    @Autowired
    private BoardRepository boardRepository;

    private BoardDTO checkBoard(String boardId) {
        BoardDTO board = boardService.getBoardByBoardId(boardId);
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
        BoardDTO boardDTO = boardService.getBoardByBoardId(boardId);

        if (boardDTO == null) {
            throw new ItemNotFoundException("Board not found !!!");
        }

        // ตรวจสอบว่าบอร์ดเป็น public หรือ private
        boolean isPublic = "public".equalsIgnoreCase(boardDTO.getVisibility());

        // ถ้าเป็น public สามารถเข้าถึงได้โดยไม่ต้องใช้ token
        if (isPublic) {
            boardDTO.setAccessRight("READ");
            return ResponseEntity.ok(boardDTO);
        }

        // ถ้าไม่มี token หรือ token ไม่ถูกต้อง ให้ return 403 Forbidden
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied! Private board requires authentication.");
        }

        String jwtToken = token.substring(7);
        String userId = jwtService.getOidFromToken(jwtToken);

        // ตรวจสอบว่าผู้ใช้เป็นเจ้าของบอร์ดหรือไม่
        boolean isOwner = boardDTO.getOwner() != null && boardDTO.getOwner().getUserId().equals(userId);

        if (isOwner) {
            boardDTO.setAccessRight("OWNER");
            return ResponseEntity.ok(boardDTO); // ส่งข้อมูลบอร์ดกลับไป
        }

        // ตรวจสอบว่า userId เป็น collaborator หรือไม่
        Collaborators collaborator = collabService.findCollaboratorByUserIdAndBoardId(userId, boardId);
        if (collaborator != null) {
            boardDTO.setAccessRight(collaborator.getAccessRight().name()); // ตั้งค่า accessRight สำหรับ collaborator
            return ResponseEntity.ok(boardDTO); // ส่งข้อมูลบอร์ดกลับไปถ้าเป็น collaborator
        }

        throw new AccessDeniedException("Access denied");
    }



    @GetMapping("")
    public ResponseEntity<?> getBoards(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            String oid = jwtService.getOidFromToken(jwtToken);

            Map<String, List<BoardDTO>> boards = boardService.getPersonalAndCollabBoards(oid);
            return ResponseEntity.ok(boards);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


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

            if (addBoardDTO.getName().length() > 120){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "board name is longer than limit"));
            }

            String afterSubToken = token.substring(7);
            String oid;
            try {
                oid = jwtService.getOidFromToken(afterSubToken);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid token"));
            }


            AddBoardDTO newBoard = new AddBoardDTO();
            newBoard.setName(addBoardDTO.getName());
            UserLocal owner = userService.findByOid(oid);
            newBoard.setOwner(owner);

            AddBoardDTO createdBoard = boardService.addBoard(newBoard,afterSubToken);
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
        // ตรวจสอบว่าเจอบอร์ดหรือไม่
        BoardDTO boardEntity = boardService.getBoardByBoardId(boardId);

        // ถ้าไม่เจอบอร์ด ให้ส่ง response ว่าไม่พบข้อมูล
        if (boardEntity == null) {
            throw new ItemNotFoundException("Board not found !!!");
        }

        // ถ้าไม่มี token และบอร์ดเป็น private ให้ return 403
        boolean isPrivateBoard = "private".equalsIgnoreCase(boardEntity.getVisibility());
        if (token == null && isPrivateBoard) {
            throw new AccessDeniedException("Access denied! Private board requires authentication.");
        }

        // ตรวจสอบว่าถ้ามี token ให้ทำการดึง userId
        String userId = null;
        boolean isOwner = false;
        if (token != null) {
            String jwtToken = token.substring(7); // ตัดคำว่า "Bearer " ออก
            userId = jwtService.getOidFromToken(jwtToken);

            // ตรวจสอบว่าผู้ใช้เป็นเจ้าของบอร์ดหรือไม่
            isOwner = boardEntity.getOwner() != null && boardEntity.getOwner().getUserId().equals(userId);
        }

        // กรณีที่บอร์ดเป็น public และผู้ใช้ไม่ใช่เจ้าของ ให้ return 403 Forbidden
        if (!isPrivateBoard && !isOwner) {
            throw new AccessDeniedException("Access denied! You are not authorized to update the visibility of this public board.");
        }

        // กรณีที่บอร์ดเป็น private และ user ไม่ใช่ owner ให้ return 403
        if (isPrivateBoard && !isOwner) {
            throw new AccessDeniedException("Access denied! You are not the owner of this private board.");
        }

        // กรณีที่เป็น owner และไม่ได้ส่ง body หรือ requestBody ไม่มีค่า visibility ให้ return 400
        if (isOwner && (body == null || !body.containsKey("visibility"))) {
            throw new BadRequestException("Visibility cannot be null or missing.");
        }

        // ตรวจสอบว่า visibility มีค่าเป็น public หรือ private หรือไม่
        String visibility = body.get("visibility");
        if (visibility == null || (!"private".equalsIgnoreCase(visibility) && !"public".equalsIgnoreCase(visibility))) {
            throw new BadRequestException("Invalid visibility value.");
        }

        // อัปเดตสถานะการมองเห็นใน entity
        BoardVisi boardVisibility = BoardVisi.valueOf(visibility.toUpperCase());
        boardEntity.setVisibility(String.valueOf(boardVisibility));

        // บันทึกการเปลี่ยนแปลงลงฐานข้อมูล
        boardService.updateBoard(boardEntity);  // เรียก method เพื่อบันทึกการเปลี่ยนแปลง

        // สร้าง response object
        BoardUpdateDTO response = new BoardUpdateDTO(
                boardEntity.getId(),
                "Board visibility updated successfully",
                boardEntity.getVisibility()
        );

        return ResponseEntity.ok(response);
    }
}


