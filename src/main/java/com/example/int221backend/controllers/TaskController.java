package com.example.int221backend.controllers;

import com.example.int221backend.dtos.SimpleTaskDTO;
import com.example.int221backend.entities.Task;
import com.example.int221backend.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @Autowired
    private ModelMapper modelMapper;

//    @GetMapping("")
//    public ResponseEntity<Object> getAllTask(){
//        List<Task> task = service.getAllTask();
//        SimpleTaskDTO simpleTask = modelMapper.map(task, SimpleTaskDTO.class);
//        return ResponseEntity.ok(simpleTask);
//    }

    @GetMapping("")
    public ResponseEntity<Object> getAllTask(){
        List<Task> tasks = service.getAllTask();
        List<SimpleTaskDTO> simpleTaskDTOs = tasks.stream()
                .map(task -> modelMapper.map(task, SimpleTaskDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(simpleTaskDTOs);
    }
}
