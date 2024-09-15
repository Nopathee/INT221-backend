package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV3DTO;
import com.example.int221backend.dtos.SimpleTaskV3DTO;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.services.BoardService;
import com.example.int221backend.services.StatusV3Service;
import com.example.int221backend.services.TaskV3Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private ModelMapper modelMapper;

    @Autowired
    private BoardService boardService;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(
            @PathVariable String boardId,
            @RequestParam(required = false) Set<String> filterStatuses) {

        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }

        List<TaskV3> tasks = taskV3Service.getAllTask(boardId, filterStatuses);
        List<SimpleTaskV3DTO> simpleTaskV3DTOs = tasks.stream()
                .map(task -> modelMapper.map(task, SimpleTaskV3DTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(simpleTaskV3DTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable String boardId, @PathVariable Integer id) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }

        TaskV3 task = taskV3Service.getTaskById(id);
        if (task == null || !task.getBoard().getBoardId().equals(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found in the specified board");
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<?> addTask(@PathVariable String boardId, @RequestBody AddTaskDTO addTaskDTO) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
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

            if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().trim().isEmpty()){
                addTaskDTO.setDescription(null);
            }

            if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().trim().isEmpty()){
                addTaskDTO.setAssignees(null);
            }

            Integer status = addTaskDTO.getStatus();

            AddTaskV3DTO newTask = taskV3Service.addTask(addTaskDTO, status);

            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String boardId, @PathVariable Integer id) {
        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
        }

        if (!taskV3Service.isTaskInBoard(id, boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found in the specified board");
        }

        taskV3Service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId, @PathVariable Integer id, @RequestBody AddTaskDTO task) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
            }

            if (task.getTitle() != null) {
                task.setTitle(task.getTitle().trim());
            }

            if (task.getDescription() != null) {
                if (task.getDescription().isEmpty()) {
                    task.setDescription(null);
                } else {
                    task.setDescription(task.getDescription().trim());
                }
            }

            if (task.getAssignees() != null) {
                if (task.getAssignees().isEmpty()) {
                    task.setAssignees(null);
                } else {
                    task.setAssignees(task.getAssignees().trim());
                }
            }

            Integer status = task.getStatus();

            TaskV3 editedTask = modelMapper.map(task, TaskV3.class);

            if (!taskV3Service.isTaskInBoard(id, boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found in the specified board");
            }

            AddTaskV3DTO updatedTask = taskV3Service.updateTask(editedTask, id, status);

            return ResponseEntity.ok(updatedTask);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
