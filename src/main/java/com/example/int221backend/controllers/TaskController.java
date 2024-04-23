package com.example.int221backend.controllers;

import com.example.int221backend.entities.Task;
import com.example.int221backend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/tasks")
public class TaskController {
    @Autowired
    private TaskService service;

    @GetMapping("")
    public List<Task> getAllTask(){
        return service.getAllTask();
    }
}
