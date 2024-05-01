package com.example.int221backend.services;

import com.example.int221backend.entities.Task;
import com.example.int221backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public Task getTaskById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + id + "DOES NOT EXIST !!!") {
        });
    }

    @Transactional
    public Task addTask(Task task) {
        return repository.save(task);
    }

    @Transactional
    public void deleteTask(Integer taskId) {
        Task task = repository.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
        repository.delete(task);
    }
}
