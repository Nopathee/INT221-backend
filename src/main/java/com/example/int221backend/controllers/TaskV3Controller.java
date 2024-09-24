package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV2DTO;
import com.example.int221backend.dtos.SimpleTaskV3DTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.JwtService;
import com.example.int221backend.services.StatusV3Service;
import com.example.int221backend.services.TaskV3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v3/boards/{boardId}/tasks")
public class TaskV3Controller {

    @Autowired
    private TaskV3Service taskV3Service;

    @Autowired
    private StatusV3Service statusService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BoardService boardService;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(
            @PathVariable String boardId,
            @RequestParam(required = false) Set<String> filterStatuses) {

        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        List<TaskV3> tasks = taskV3Service.getAllTask(filterStatuses, boardId);
        List<SimpleTaskV3DTO> simpleTaskV3DTOs = tasks.stream()
                .map(task -> modelMapper.map(task, SimpleTaskV3DTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(simpleTaskV3DTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(
            @PathVariable String boardId,
            @PathVariable Integer id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Extract and validate token
            String jwtToken = token.substring(7);
            String userId = jwtService.getOidFromToken(jwtToken);

            // Check if the board exists
            Board board = boardService.getBoardByBoardId(boardId);
            System.out.println(id);
            // Check if the task exists
            TaskV3 task = taskV3Service.getTaskById(id, boardId);

            // Return the task if everything is valid
            return ResponseEntity.ok(modelMapper.map(task, SimpleTaskV3DTO.class));

        } catch (ResponseStatusException e) {
            // Catch and return a proper status for token-related issues or other exceptions
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addTask(@PathVariable String boardId, @RequestBody AddTaskDTO addTaskDTO) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Board not found"));
            }

            if (addTaskDTO.getTitle() != null) {
                addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
            }
            if (addTaskDTO.getDescription() != null) {
                addTaskDTO.setDescription(addTaskDTO.getDescription().trim());
            }
            if (addTaskDTO.getAssignees() != null) {
                addTaskDTO.setAssignees(addTaskDTO.getAssignees().trim());
            }

            if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().isEmpty()) {
                addTaskDTO.setDescription(null);
            }

            if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().isEmpty()) {
                addTaskDTO.setAssignees(null);
            }

            Integer statusId = addTaskDTO.getStatus();
            AddTaskV2DTO newTask = taskV3Service.addTask(addTaskDTO, statusId, boardId);

            return new ResponseEntity<>(newTask, HttpStatus.CREATED);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String boardId, @PathVariable Integer id) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Board not found"));
            }

            taskV3Service.deleteTask(id, boardId);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId, @PathVariable Integer id, @RequestBody AddTaskDTO addTaskDTO) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Board not found"));
            }

            if (addTaskDTO.getTitle() != null) {
                addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
            }
            if (addTaskDTO.getDescription() != null) {
                addTaskDTO.setDescription(addTaskDTO.getDescription().isEmpty() ? null : addTaskDTO.getDescription().trim());
            }
            if (addTaskDTO.getAssignees() != null) {
                addTaskDTO.setAssignees(addTaskDTO.getAssignees().isEmpty() ? null : addTaskDTO.getAssignees().trim());
            }

            Integer status = addTaskDTO.getStatus();
            TaskV3 editedTask = modelMapper.map(addTaskDTO, TaskV3.class);

            AddTaskV2DTO updatedTask = taskV3Service.updateTask(editedTask, id, status, boardId);
            return ResponseEntity.ok(updatedTask);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }
}
