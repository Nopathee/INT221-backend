package com.example.int221backend.services;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.Task;
import com.example.int221backend.repositories.StatusRepository;
import com.example.int221backend.repositories.TaskRepository;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;
    @Autowired
    private TaskRepository repository;

    @Autowired
    private StatusRepository statusRepository;

    public List<Task> getAllTask() {
        return repository.findAll();
    }

    public Task getTaskById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + id + "DOES NOT EXIST !!!") {
                });
    }

    @Transactional
    public AddTaskDTO addTask(AddTaskDTO addTaskDTO) {

        if (addTaskDTO.getStatus() == null) {
            throw new IllegalArgumentException("Status ID is required for add a task.");
        }

        Status status = statusRepository.findById(addTaskDTO.getStatus())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status with ID " + addTaskDTO.getStatus() + " not found."));
        Task task = modelMapper.map(addTaskDTO, Task.class);
        task.setStatus(status);

        return modelMapper.map(repository.saveAndFlush(task), addTaskDTO.getClass());
    }

    @Transactional
    public void deleteTask(Integer taskId) {
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
        repository.delete(task);
    }

    @Transactional
    public AddTaskDTO updateTask(AddTaskDTO addTaskDTO, Integer taskId) {
        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
        }
        Task existingTask = repository.findById(taskId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST!!!"));

        if (addTaskDTO.getStatus() == null) {
            throw new IllegalArgumentException("Status id is required");
        }

        Status status = statusRepository.findById(addTaskDTO.getStatus())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status with ID " + addTaskDTO.getStatus() + " not found."));

        String id = existingTask.getId();
        modelMapper.map(addTaskDTO, existingTask);
        existingTask.setId(id);
        existingTask.setStatus(status);
        Task updatedTask = repository.saveAndFlush(existingTask);

        return modelMapper.map(updatedTask, AddTaskDTO.class);
    }
}
