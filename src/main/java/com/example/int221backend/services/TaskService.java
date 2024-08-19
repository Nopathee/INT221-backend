//package com.example.int221backend.services;
//
//import com.example.int221backend.dtos.AddTaskDTO;
//import com.example.int221backend.dtos.AddTaskV1DTO;
//import com.example.int221backend.entities.Task;
//import com.example.int221backend.repositories.TaskRepository;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@Service
//public class TaskService {
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private TaskRepository repository;
//
//    public List<Task> getAllTask() {
//        return repository.findAll();
//    }
//
//    public Task getTaskById(Integer id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + id + "DOES NOT EXIST !!!") {
//                });
//    }
//
//    @Transactional
//    public AddTaskV1DTO addTask(AddTaskV1DTO addTaskV1DTO) {
//        Task task = modelMapper.map(addTaskV1DTO, Task.class);
//        return modelMapper.map(repository.saveAndFlush(task), addTaskV1DTO.getClass());
//    }
//    @Transactional
//    public void deleteTask(Integer taskId) {
//        Task task = repository.findById(taskId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
//        repository.delete(task);
//    }
//
//    @Transactional
//    public AddTaskV1DTO updateTask(AddTaskV1DTO addTaskDTO, Integer taskId) {
//        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()){
//            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
//        }
//        Task existingTask = repository.findById(taskId)
//                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,"TASK ID " + taskId + " DOES NOT EXIST!!!"));
//
//        String id = existingTask.getId();
//        modelMapper.map(addTaskDTO,existingTask);
//        existingTask.setId(id);
//        Task updatedTask = repository.saveAndFlush(existingTask);
//        AddTaskV1DTO updatedTaskDTO = modelMapper.map(updatedTask,AddTaskV1DTO.class);
//
//        return updatedTaskDTO;
//    }
//}