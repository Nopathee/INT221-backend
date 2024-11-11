package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.dtos.BoardDTO;
import com.example.int221backend.dtos.BoardIdDTO;
import com.example.int221backend.dtos.StatusAndTaskCDTO;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.BoardVisi;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th","https://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards/{boardId}/statuses")
public class StatusV3Controller {

    @Autowired
    private StatusV3Service statusService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CollabService collabService;

    @Autowired
    private AccessControlService accessControlService;

    @GetMapping("")
    public ResponseEntity<Object> getAllStatuses(
            @PathVariable String boardId,
            @RequestHeader(value = "Authorization", required = false) String token
    ){      BoardDTO boardDTO = boardService.getBoardByBoardId(boardId);
        if (boardDTO == null) {
        throw new ItemNotFoundException("Board not found !!!");
    }

    BoardVisi visibility = BoardVisi.valueOf(boardDTO.getVisibility().toUpperCase());
        if (visibility == BoardVisi.PUBLIC) {
        List<StatusAndTaskCDTO> statuses = statusService.getAllStatus(boardId);
        return ResponseEntity.ok(statuses);
    }

    String userId = null;
        if (token != null && token.startsWith("Bearer ")) {
        String jwtToken = token.substring(7);
        userId = jwtService.getOidFromToken(jwtToken);
    }

    boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.READ);
        if (!hasAccess) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied! You do not have permission to access this board.");
    }

    List<StatusAndTaskCDTO> statuses = statusService.getAllStatus(boardId);
        return ResponseEntity.ok(statuses);
    }



    @GetMapping("/{statusId}")
    public ResponseEntity<?> getStatusById(
            @PathVariable String boardId,
            @PathVariable Integer statusId,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        BoardDTO boardDTO = boardService.getBoardByBoardId(boardId);
        if (boardDTO == null) {
            throw new ItemNotFoundException("Board not found !!!");
        }

        BoardVisi visibility = BoardVisi.valueOf(boardDTO.getVisibility().toUpperCase());
        // ถ้าเป็นบอร์ด public สามารถเข้าถึงได้โดยไม่ต้องใช้ token
        if (visibility == BoardVisi.PUBLIC) {
            List<StatusAndTaskCDTO> statuses = statusService.getAllStatus(boardId);
            return ResponseEntity.ok(statuses);
        }

        // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
        String userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            userId = jwtService.getOidFromToken(jwtToken);
        }

        boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.READ);
        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied! You do not have permission to access this board.");
        }

        // ถ้ามีสิทธิ์เข้าถึงบอร์ดนี้ ให้ดึงสถานะตาม boardId และ statusId
        Status status = statusService.getStatusById(statusId, boardId);
        if (status != null){
            return ResponseEntity.ok(status);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied! You are not the owner of this private board.");
    }


    @PostMapping("")
    public ResponseEntity<?> addStatus(@PathVariable String boardId, @RequestBody(required = false) AddStatusDTO statusDTO, @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        try {
            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);

            if (statusDTO == null || statusDTO.getName() == null) {
                if (hasAccess){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }

            }

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied to private board"));
            }

            AddStatusDTO newStatus = statusService.addStatus(statusDTO, boardId);
            return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }


    @PutMapping("/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody(required = false) AddStatusDTO statusDTO, @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }

        // Check if the request body is null or empty


        try {
            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);
            if ((statusDTO == null || statusDTO.getName() == null) && statusService.getStatusById(statusId,boardId) != null) {
                if (hasAccess){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }
            }
            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied to private board"));
            }

            Status updatedStatus = statusService.editStatus(modelMapper.map(statusDTO, Status.class), statusId);
            return ResponseEntity.ok(modelMapper.map(updatedStatus, AddStatusDTO.class));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<?> deleteStatus(@PathVariable String boardId, @PathVariable Integer statusId, @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }
        try {

            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);

            if (hasAccess) {
                statusService.deleteStatus(statusId);
                return ResponseEntity.ok().build();
            } else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<?> deleteAndTransferStatus(@PathVariable String boardId, @PathVariable Integer id, @PathVariable Integer newId, @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, id) || !statusService.existsStatusInBoard(boardId, newId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }

        try {

            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);

            if (hasAccess) {
                statusService.deleteAndTranStatus(id, newId);
                return ResponseEntity.ok().build();
            } else {
                throw new ForBiddenException("Access denies");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }
}
