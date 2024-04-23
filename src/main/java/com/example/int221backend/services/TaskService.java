package com.example.int221backend.services;

import com.example.int221backend.entities.Task;
import com.example.int221backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    public List<Task> getAllTask() {
        return repository.findAll();
    }
}
