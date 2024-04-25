package com.example.int221backend.services;

import com.example.int221backend.entities.Task;
import com.example.int221backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public Task getTaskById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Task id" + id + "DOES NOT EXIST !!!"){
        });
    }
}
