package com.example.int221backend.controllers;

import com.example.int221backend.dtos.*;
import com.example.int221backend.entities.AccessRight;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.exception.AccessDeniedException;
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ForBiddenException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.TaskV3Repository;
import com.example.int221backend.services.*;
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

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th","https://intproj23.sit.kmutt.ac.th"})
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

    @Autowired
    private CollabService collabService;

    @Autowired
    private AccessControlService accessControlService;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(
            @PathVariable String boardId,
            @RequestParam(required = false) Set<String> filterStatuses,
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (!boardService.existsById(boardId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Board not found"));
        }

        BoardDTO board = boardService.getBoardByBoardId(boardId);
        if (board == null){
            throw new ItemNotFoundException("board not found!!!!");
        }

        String oId = null;

        if (token != null && token.startsWith("Bearer ")) {
            String afterSubToken = token.substring(7);
            oId = jwtService.getOidFromToken(afterSubToken);
        }

        boolean hasAccess = accessControlService.hasAccess(oId,boardId,token, AccessRight.READ);
        if (!hasAccess){
            throw new AccessDeniedException("Access Denied!");
        }

        List<TaskV3> tasks = taskV3Service.getAllTask(filterStatuses, boardId);

        List<AddTaskV2DTO> taskV2DTOList = tasks.stream()
                .map(task -> {
                    AddStatusV3DTO statusDto = new AddStatusV3DTO();
                    Status status = task.getStatus();  // Assuming `TaskV3` has a `Status` field

                    if (status != null) {
                        statusDto.setId(status.getId());
                        statusDto.setName(status.getName());
                        statusDto.setDescription(status.getDescription());
                        statusDto.setBId(status.getBoard().getBoardId());
                        statusDto.setColor(status.getColor());
                    }

                    AddTaskV2DTO addTaskV2DTO= new AddTaskV2DTO();
                    addTaskV2DTO.setId(task.getId());
                    addTaskV2DTO.setTitle(task.getTitle());
                    addTaskV2DTO.setDescription(task.getDescription());
                    addTaskV2DTO.setAssignees(task.getAssignees());
                    addTaskV2DTO.setStatus(statusDto);

                    return addTaskV2DTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(taskV2DTOList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(
            @PathVariable String boardId,
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        BoardDTO boardDTO = boardService.getBoardByBoardId(boardId);
        if (boardDTO == null) {
            throw new ItemNotFoundException("Board not found !!!");
        }

        // ถ้า token ถูกส่งเข้ามา ดึง userId จาก JWT token
        String userId = null;
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            userId = jwtService.getOidFromToken(jwtToken);
        }

        // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
        boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.READ);
        // ถ้าผู้ใช้ไม่มีสิทธิ์เข้าถึงบอร์ดนี้ ให้ส่ง response ว่า access denied
        if (!hasAccess) {
            throw new AccessDeniedException("Access denied!!");
        }

        TaskV3 task = taskV3Service.getTaskById(id, boardId);
        if (task == null) {
            throw new ItemNotFoundException("Task not found !!!");
        }

        AddTaskV2DTO showTask = new AddTaskV2DTO();
        AddStatusV3DTO showStatus = new AddStatusV3DTO();
        Status status = task.getStatus();
        if (status != null) {
            showStatus.setId(status.getId());
            showStatus.setName(status.getName());
            showStatus.setDescription(status.getDescription());
            showStatus.setBId(status.getBoard().getBoardId());
            showStatus.setColor(status.getColor());
        }
        showTask.setId(task.getId());
        showTask.setTitle(task.getTitle());
        showTask.setDescription(task.getDescription());
        showTask.setAssignees(task.getAssignees());
        showTask.setStatus(showStatus);

        return ResponseEntity.ok(showTask);
    }



    @PostMapping("")
    public ResponseEntity<?> addTask(@PathVariable String boardId, @RequestBody(required = false) AddTaskDTO addTaskDTO ,@RequestHeader("Authorization") String token) {

            if (addTaskDTO != null){
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
            }

            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);
            // ถ้าผู้ใช้ไม่มีสิทธิ์เข้าถึงบอร์ดนี้ ให้ส่ง response ว่า access denied
            if (addTaskDTO == null){
                if (hasAccess){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Collections.singletonMap("error", "Access denied, request body required"));
                }
            }
            if (hasAccess){
                Integer statusId = addTaskDTO.getStatus();
                AddTaskV2DTO newTask = taskV3Service.addTask(addTaskDTO, statusId, boardId);
                newTask.getStatus().setBId(boardId);
                return new ResponseEntity<>(newTask, HttpStatus.CREATED);
            }else {
                throw new ForBiddenException("Access denies");
            }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String boardId, @PathVariable String id,@RequestHeader("Authorization") String token) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Board not found"));
            }

            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);

            if (hasAccess){
                taskV3Service.deleteTask(id, boardId);
                return ResponseEntity.ok().build();
            }else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String boardId, @PathVariable String id, @RequestBody(required = false) AddTaskDTO addTaskDTO,@RequestHeader("Authorization") String token) {
        try {
            if (!boardService.existsById(boardId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Board not found"));
            }
            if (addTaskDTO != null){
                if (addTaskDTO.getTitle() != null) {
                    addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
                }
                if (addTaskDTO.getDescription() != null) {
                    addTaskDTO.setDescription(addTaskDTO.getDescription().isEmpty() ? null : addTaskDTO.getDescription().trim());
                }
                if (addTaskDTO.getAssignees() != null) {
                    addTaskDTO.setAssignees(addTaskDTO.getAssignees().isEmpty() ? null : addTaskDTO.getAssignees().trim());
                }
            }

            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                userId = jwtService.getOidFromToken(jwtToken);
            }

            // ตรวจสอบสิทธิ์การเข้าถึงบอร์ดโดยใช้ AccessControlService
            boolean hasAccess = accessControlService.hasAccess(userId, boardId, token, AccessRight.WRITE);
            if (addTaskDTO == null){
                if (!hasAccess){
                    throw new ForBiddenException("Access denied, request body required");
                } else if (!taskV3Service.existingTask(id,boardId)) {
                    throw new ItemNotFoundException("task not found!");
                } else {
                    throw new BadRequestException("Access denied, request body required");
                }
            }
            if (hasAccess){
                assert addTaskDTO != null;
                Integer status = addTaskDTO.getStatus();
                TaskV3 editedTask = modelMapper.map(addTaskDTO, TaskV3.class);

                AddTaskV2DTO updatedTask = taskV3Service.updateTask(editedTask, id, status, boardId);
                return ResponseEntity.ok(updatedTask);

            }else {
                throw new ForBiddenException("Access denies");
            }


        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Collections.singletonMap("error", e.getReason()));
        }
    }
}
