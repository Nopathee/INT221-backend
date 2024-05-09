package com.example.int221backend.services;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV2DTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.Task;
import com.example.int221backend.entities.TaskV2;
import com.example.int221backend.repositories.StatusRepository;
import com.example.int221backend.repositories.TaskV2Repository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskV2Service {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TaskV2Repository repository;

    @Autowired
    private StatusRepository statusRepository;

    public List<TaskV2> getAllTask() {
        return repository.findAll();
    }

    public TaskV2 getTaskById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + id + "DOES NOT EXIST !!!") {
                });
    }

    @Transactional
    public AddTaskV2DTO addTask(AddTaskDTO addTaskV2DTO, String statusId){

        final String finalStatusId;

        if (statusId == null) {
            finalStatusId = "1";
        } else {
            finalStatusId = statusId;
        }

        Status status = statusRepository.findById(finalStatusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status ID " + finalStatusId + " Does Not EXIST!!!"));


        TaskV2 task = modelMapper.map(addTaskV2DTO, TaskV2.class);
        task.setStatus(status);

        AddTaskV2DTO taskV2DTO = modelMapper.map(task,AddTaskV2DTO.class);

        return modelMapper.map(repository.saveAndFlush(task),taskV2DTO.getClass());
    }

    @Transactional
    public void deleteTask(Integer taskId) {
        TaskV2 task = repository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
        repository.delete(task);
    }

//    @Transactional
//    public AddTaskV2DTO updateTask(AddTaskV2DTO addTaskDTO, Integer taskId) {
//        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
//            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
//        }
//        TaskV2 existingTask = repository.findById(taskId)
//                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST!!!"));
//
//        if (addTaskDTO.getStatus() == null) {
//            throw new IllegalArgumentException("Status id is required");
//        }
//
//        Status status = statusRepository.findById(addTaskDTO.getStatus())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status with ID " + addTaskDTO.getStatus() + " not found."));
//
//        String id = existingTask.getId();
//        modelMapper.map(addTaskDTO, existingTask);
//        existingTask.setId(id);
//        existingTask.setStatus(status);
//        TaskV2 updatedTask = repository.saveAndFlush(existingTask);
//
//        return modelMapper.map(updatedTask, AddTaskV2DTO.class);
//    }

    @Transactional
    public AddTaskV2DTO updateTask(AddTaskDTO addTaskDTO, Integer taskId , String statusId){
        final String finalStatusId;

        if (statusId == null) {
            finalStatusId = "1";
        } else {
            finalStatusId = statusId;
        }

        Status status = statusRepository.findById(finalStatusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status ID " + finalStatusId + " Does Not EXIST!!!"));

        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
        }

        TaskV2 existingTask = repository.findById(taskId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST!!!"));

        String id = existingTask.getId();
        modelMapper.map(addTaskDTO, existingTask);
        existingTask.setId(id);
        existingTask.setStatus(status);
        TaskV2 updatedTask = repository.saveAndFlush(existingTask);

        return modelMapper.map(updatedTask, AddTaskV2DTO.class);
    }
}
