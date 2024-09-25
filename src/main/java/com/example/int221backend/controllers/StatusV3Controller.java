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
    public ResponseEntity<Object> getAllStatuses(@PathVariable String boardId) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        List<Status> statuses = statusService.getAllStatus(boardId);
        List<AddStatusDTO> statusDTOs = statuses.stream()
                .map(status -> modelMapper.map(status, AddStatusDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(statusDTOs);
    }

    @GetMapping("/{statusId}")
    public ResponseEntity<?> getStatusById(@PathVariable String boardId, @PathVariable Integer statusId , @RequestHeader ("Authorization") String token){
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, statusId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }
        String afterSubToken = token.substring(7);

        String oid = jwtService.getOidFromToken(afterSubToken);

        Board board = boardService.getBoardByBoardId(boardId);
        boolean isPublic = board.getVisibility().toString().equalsIgnoreCase("public");
        boolean isOwner = board.getOwner().getOid().equals(oid);

        if (isOwner || isPublic){
            Status status = statusService.getStatusById(statusId);
            return ResponseEntity.ok(modelMapper.map(status, AddStatusDTO.class));
        }else {
            throw new ForBiddenException("Access denies");
        }


    }

    @PostMapping("")
    public ResponseEntity<?> addStatus(@PathVariable String boardId, @RequestBody AddStatusDTO statusDTO,@RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        try {

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner){
                AddStatusDTO newStatus = statusService.addStatus(statusDTO, boardId);
                return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
            }else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody AddStatusDTO statusDTO , @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, statusId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }

        try {

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner){
                Status updatedStatus = statusService.editStatus(modelMapper.map(statusDTO, Status.class), statusId);
                return ResponseEntity.ok(modelMapper.map(updatedStatus, AddStatusDTO.class));
            }else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @DeleteMapping("/{statusId}")
    public ResponseEntity<?> deleteStatus(@PathVariable String boardId, @PathVariable Integer statusId , @RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, statusId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }

        try {

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner){
                statusService.deleteStatus(statusId);
                return ResponseEntity.ok().build();
            }else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<?> deleteAndTransferStatus(@PathVariable String boardId, @PathVariable Integer id, @PathVariable Integer newId ,@RequestHeader("Authorization") String token) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, id) || !statusService.existsStatusInBoard(boardId, newId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board or Status not found"));
        }

        try {

            String afterSubToken = token.substring(7);

            String oid = jwtService.getOidFromToken(afterSubToken);

            Board board = boardService.getBoardByBoardId(boardId);
            boolean isOwner = board.getOwner().getOid().equals(oid);

            if (isOwner){
                statusService.deleteAndTranStatus(id, newId);
                return ResponseEntity.ok().build();
            }else {
                throw new ForBiddenException("Access denies");
            }
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }
}
