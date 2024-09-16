package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.StatusV3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("")
    public ResponseEntity<Object> getAllStatuses(@PathVariable String boardId) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }

        List<Status> statuses = statusService.getAllStatus(boardId);
        List<AddStatusDTO> statusDTOs = statuses.stream()
                .map(status -> modelMapper.map(status, AddStatusDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(statusDTOs);
    }

    @PostMapping("")
    public ResponseEntity<?> addStatus(@PathVariable String boardId, @RequestBody AddStatusDTO statusDTO) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }

        try {
            AddStatusDTO newStatus = statusService.addStatus(statusDTO, boardId);
            return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/{statusId}")
    public ResponseEntity<?> updateStatus(@PathVariable String boardId, @PathVariable Integer statusId, @RequestBody AddStatusDTO statusDTO) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, statusId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board or Status not found");
        }

        try {
            Status updatedStatus = statusService.editStatus(modelMapper.map(statusDTO, Status.class), statusId);
            return ResponseEntity.ok(modelMapper.map(updatedStatus, AddStatusDTO.class));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }


    @DeleteMapping("/{statusId}")
    public ResponseEntity<String> deleteStatus(@PathVariable String boardId, @PathVariable Integer statusId) {
        if (!boardService.existsById(boardId) || !statusService.existsStatusInBoard(boardId, statusId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board or Status not found");
        }

        try {
            statusService.deleteStatus(statusId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }


}
