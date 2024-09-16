package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV2DTO;
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

        List<TaskV3> tasks = taskV3Service.getAllTask(filterStatuses,boardId);
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

        TaskV3 task = taskV3Service.getTaskById(id, boardId );
        if (task == null || !task.getBoard().getBoardId().equals(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found in the specified board");
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<?> addTask(@PathVariable String boardId, @RequestBody AddTaskDTO addTaskDTO){
        try {
            if (!boardService.existsById(boardId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
            }
            if (addTaskDTO.getTitle() != null){
                addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
            }
            if (addTaskDTO.getDescription() != null){
                addTaskDTO.setDescription(addTaskDTO.getDescription().trim());
            }
            if (addTaskDTO.getAssignees() != null){
                addTaskDTO.setAssignees(addTaskDTO.getAssignees().trim());
            }

            if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().trim().isEmpty()){
                addTaskDTO.setDescription(null);
            }

            if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().trim().isEmpty()){
                addTaskDTO.setAssignees(null);
            }

            Integer statusId = addTaskDTO.getStatus();

            AddTaskV2DTO newTask = taskV3Service.addTask(addTaskDTO, statusId, boardId);

            return new ResponseEntity<>(newTask, HttpStatus.CREATED);

        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String boardId, @PathVariable Integer id){
        try {
            if (!boardService.existsById(boardId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
            }

            taskV3Service.deleteTask(id,boardId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId , @PathVariable Integer id , @RequestBody AddTaskDTO addTaskDTO) {
        try {
            if (!boardService.existsById(boardId)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Board not found");
            }

            if (addTaskDTO.getTitle() != null) {
                addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
            }

            if (addTaskDTO.getDescription() != null) {
                if (addTaskDTO.getDescription().isEmpty()) {
                    addTaskDTO.setDescription(null);
                } else {
                    addTaskDTO.setDescription(addTaskDTO.getDescription().trim());
                }

            }

            if (addTaskDTO.getAssignees() != null) {

                if (addTaskDTO.getAssignees().isEmpty()) {
                    addTaskDTO.setAssignees(null);
                } else {
                    addTaskDTO.setAssignees(addTaskDTO.getAssignees().trim());
                }
            }

            Integer status = addTaskDTO.getStatus();

            TaskV3 editedTask = modelMapper.map(addTaskDTO, TaskV3.class);

            AddTaskV2DTO updatedTask = taskV3Service.updateTask(editedTask,id,status,boardId);

            return ResponseEntity.ok(updatedTask);
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
