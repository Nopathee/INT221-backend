package com.example.int221backend.controllers;

import com.example.int221backend.dtos.*;
import com.example.int221backend.dtos.AddTaskV2DTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.Task;
import com.example.int221backend.entities.TaskV2;
import com.example.int221backend.entities.TaskStatus;
import com.example.int221backend.repositories.StatusRepository;
import com.example.int221backend.services.StatusService;
import com.example.int221backend.services.TaskV2Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v2/tasks")
public class TaskV2Controller {
    @Autowired
    private TaskV2Service service;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(@RequestParam(required = false) Set<String> filterStatuses) {
//        List<TaskV2> tasks = service.getAllTask();
//        List<SimpleTaskV2DTO> simpleTaskV2DTOs = tasks.stream()
//                .map(task -> modelMapper.map(task, SimpleTaskV2DTO.class))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(simpleTaskV2DTOs);
        List<TaskV2> tasks = service.getAllTask(filterStatuses);
        List<SimpleTaskV2DTO> simpleTaskV2DTOs = tasks.stream()
                .map(task -> modelMapper.map(task, SimpleTaskV2DTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleTaskV2DTOs);
    }

    @GetMapping("/{id}")
    public TaskV2 getTaskById(@PathVariable Integer id) {
        return service.getTaskById(id);
    }


    @PostMapping("")
    public ResponseEntity<?> addTask(@RequestBody AddTaskDTO addTaskDTO) {
        try {
            if (addTaskDTO.getTitle() != null) {
                addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
            }

            if (addTaskDTO.getDescription() != null) {
                addTaskDTO.setDescription(addTaskDTO.getDescription().trim());
            }

            if (addTaskDTO.getAssignees() != null) {
                addTaskDTO.setAssignees(addTaskDTO.getAssignees().trim());
            }

            if (addTaskDTO.getDescription().trim().isEmpty()){
                addTaskDTO.setDescription(null);
            }

            if (addTaskDTO.getAssignees().trim().isEmpty()){
                addTaskDTO.setAssignees(null);
            }

            Integer status = addTaskDTO.getStatus();

            AddTaskV2DTO newTask = service.addTask(addTaskDTO, status);

            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());

        }

    }


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Integer id) {
        service.deleteTask(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id, @RequestBody AddTaskDTO task) {
        try {
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

            TaskV2 editedTask = modelMapper.map(task, TaskV2.class);

            AddTaskV2DTO updatedTask = service.updateTask(editedTask, id, status);

            return ResponseEntity.ok(updatedTask);
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

    }

}
