package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.dtos.BoardIdDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import com.example.int221backend.services.StatusV3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
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

    @GetMapping("")
    public ResponseEntity<Object> getAllStatuses(
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
                List<Status> statuses = statusService.getAllStatus(boardId);
                List<AddStatusDTO> statusDTOs = statuses.stream()
                        .map(status -> modelMapper.map(status, AddStatusDTO.class))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(statusDTOs);
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

            List<Status> statuses = statusService.getAllStatus(boardId);
            List<AddStatusDTO> statusDTOs = statuses.stream()
                    .map(status -> modelMapper.map(status, AddStatusDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(statusDTOs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to retrieve statuses"));
        }
    }



    @GetMapping("/{statusId}")
    public ResponseEntity<?> getStatusById(
            @PathVariable String boardId,
            @PathVariable Integer statusId,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        if (token == null || token.isEmpty()) {
            // Assuming public boards allow access without authentication
            Board board = boardService.getBoardByBoardId(boardId);
            if (board.getVisibility().toString().equalsIgnoreCase("public")) {
                Status status = statusService.getStatusById(statusId, boardId);
                return ResponseEntity.ok(modelMapper.map(status, AddStatusDTO.class));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Access denied"));
            }
        }

        try {
            String afterSubToken = token.substring(7);
            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);
            boolean isPublic = board.getVisibility().toString().equalsIgnoreCase("public");

            if (isOwner || isPublic) {
                Status status = statusService.getStatusById(statusId, boardId);
                return ResponseEntity.ok(modelMapper.map(status, AddStatusDTO.class));
            } else {
                throw new ForBiddenException("Access denied");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }


    @PostMapping("")
    public ResponseEntity<?> addStatus(@PathVariable String boardId, @RequestBody(required = false) AddStatusDTO statusDTO, @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        if (statusDTO == null || statusDTO.getName() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Access denied, request body required"));
        }

        try {
            String afterSubToken = token.substring(7);
            String oid = jwtService.getOidFromToken(afterSubToken);
            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (!isOwner) {
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
        if (statusDTO == null || statusDTO.getName() == null) { // Add your own validation logic for required fields
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Access denied, request body required"));
        }

        try {
            String afterSubToken = token.substring(7);
            String oid = jwtService.getOidFromToken(afterSubToken);
            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (!isOwner) {
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

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner) {
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

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner) {
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
