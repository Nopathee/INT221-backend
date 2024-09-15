package com.example.int221backend.services;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV3DTO; // เปลี่ยนเป็น AddTaskV3DTO
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.entities.local.TaskV3; // เปลี่ยนเป็น TaskV3
import com.example.int221backend.repositories.local.StatusRepository;
import com.example.int221backend.repositories.local.TaskV3Repository; // เปลี่ยนเป็น TaskV3Repository
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskV3Service {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskV3Repository taskV3Repository;

    @Autowired
    private StatusRepository statusRepository;

    public List<TaskV3> getAllTask(Set<String> filterStatuses) {
        if (filterStatuses == null || filterStatuses.isEmpty()) {
            return taskV3Repository.findAll();
        } else {
            return taskV3Repository.findAll().stream()
                    .filter(task -> filterStatuses.contains(task.getStatus().getName()))
                    .collect(Collectors.toList());
        }
    }

    public TaskV3 getTaskById(Integer id) {
        return taskV3Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID " + id + " DOES NOT EXIST !!!"));
    }

    @Transactional
    public AddTaskV3DTO addTask(AddTaskDTO addTaskDTO, Integer statusId) {

        if (addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be null");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (addTaskDTO.getTitle().length() > 100) {
            errorMessage.append("Title size must be between 0 and 100. ");
        }

        if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().length() > 500) {
            errorMessage.append("Description size must be between 0 and 500. ");
        }

        if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().length() > 30) {
            errorMessage.append("Assignees size must be between 0 and 30. ");
        }

        if (errorMessage.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }

        final Integer finalStatusId = statusId == null ? 1 : statusId;

        Status status = statusRepository.findById(finalStatusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + finalStatusId + " Does Not EXIST!!!"));

        TaskV3 task = modelMapper.map(addTaskDTO, TaskV3.class);
        task.setStatus(status);

        AddTaskV3DTO taskV3DTO = modelMapper.map(task, AddTaskV3DTO.class);

        return modelMapper.map(taskV3Repository.saveAndFlush(task), taskV3DTO.getClass());
    }

    @Transactional
    public void deleteTask(Integer taskId) {
        TaskV3 task = taskV3Repository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST !!!"));
        taskV3Repository.delete(task);
    }

    @Transactional
    public AddTaskV3DTO updateTask(TaskV3 addTaskDTO, Integer taskId, Integer statusId) {
        final Integer finalStatusId;

        if (addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be null");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (addTaskDTO.getTitle().length() > 100) {
            errorMessage.append("Title size must be between 0 and 100. ");
        }

        if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().length() > 500) {
            errorMessage.append("Description size must be between 0 and 500. ");
        }

        if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().length() > 30) {
            errorMessage.append("Assignees size must be between 0 and 30. ");
        }

        if (errorMessage.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }

        if (statusId == null) {
            finalStatusId = 1;
        } else {
            finalStatusId = statusId;
        }

        Status status = statusRepository.findById(finalStatusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + finalStatusId + " Does Not EXIST!!!"));

        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
        }

        TaskV3 existingTask = taskV3Repository.findById(taskId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST !!!"));

        String id = existingTask.getId();
        modelMapper.map(addTaskDTO, existingTask);
        existingTask.setId(id);
        existingTask.setStatus(status);
        TaskV3 updatedTask = taskV3Repository.saveAndFlush(existingTask);

        return modelMapper.map(updatedTask, AddTaskV3DTO.class);
    }
}
