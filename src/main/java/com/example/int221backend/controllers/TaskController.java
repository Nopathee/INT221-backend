package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.SimpleTaskDTO;
import com.example.int221backend.entities.Task;
import com.example.int221backend.entities.TaskStatus;
import com.example.int221backend.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173","http://localhost:4173","http://ip23ssi3.sit.kmutt.ac.th"})
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

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id){
        return service.getTaskById(id);
    }

    @PostMapping("")
    public ResponseEntity<AddTaskDTO> addTask(@RequestBody AddTaskDTO addTaskDTO) {
        AddTaskDTO newTask = service.addTask(addTaskDTO);

        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }


    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Integer id){
        service.deleteTask(id);
    }
}
