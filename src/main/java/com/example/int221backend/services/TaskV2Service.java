//package com.example.int221backend.services;
//
//import com.example.int221backend.dtos.AddTaskDTO;
//import com.example.int221backend.dtos.AddTaskV2DTO;
//import com.example.int221backend.entities.local.Status;
//import com.example.int221backend.entities.local.TaskV2;
//import com.example.int221backend.repositories.local.StatusRepository;
////import com.example.int221backend.repositories.local.TaskV2Repository;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class TaskV2Service {
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private TaskV2Repository repository;
//
//    @Autowired
//    private StatusRepository statusRepository;
//
//
//    public List<TaskV2> getAllTask(Set<String> filterStatuses) {
////        return repository.findAll();
//        if (filterStatuses == null || filterStatuses.isEmpty()) {
//            return repository.findAll();
//        } else {
//            return repository.findAll().stream()
//                    .filter(task -> filterStatuses.contains(task.getStatus().getName()))
//                    .collect(Collectors.toList());
//        }
//    }
//
//    public TaskV2 getTaskById(Integer id) {
//        return repository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + id + "DOES NOT EXIST !!!"));
//    }
//
//    @Transactional("projectManagementTransactionManager")
//    public AddTaskV2DTO addTask(AddTaskDTO addTaskV2DTO, Integer statusId){
//
//        if (addTaskV2DTO.getTitle() == null || addTaskV2DTO.getTitle().trim().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be null");
//        }
//
//        StringBuilder errorMessage = new StringBuilder();
//        if (addTaskV2DTO.getTitle().length() > 100) {
//            errorMessage.append("Title size must be between 0 and 100. ");
//        }
//
//        if (addTaskV2DTO.getDescription() != null && addTaskV2DTO.getDescription().length() > 500) {
//            errorMessage.append("Description size must be between 0 and 500. ");
//        }
//
//        if (addTaskV2DTO.getAssignees() != null && addTaskV2DTO.getAssignees().length() > 30) {
//            errorMessage.append("Assignees size must be between 0 and 30. ");
//        }
//
//        if (errorMessage.length() > 0) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
//        }
//
//        final Integer finalStatusId = statusId == null ? 1 : statusId;
//
//
//        Status status = statusRepository.findById(finalStatusId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + finalStatusId + " Does Not EXIST!!!"));
//
//
//        TaskV2 task = modelMapper.map(addTaskV2DTO, TaskV2.class);
//        task.setStatus(status);
//
//        AddTaskV2DTO taskV2DTO = modelMapper.map(task,AddTaskV2DTO.class);
//
//        return modelMapper.map(repository.saveAndFlush(task),taskV2DTO.getClass());
//    }
//
//    @Transactional("projectManagementTransactionManager")
//    public void deleteTask(Integer taskId) {
//        TaskV2 task = repository.findById(taskId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
//        repository.delete(task);
//    }
//
//    @Transactional("projectManagementTransactionManager")
//    public AddTaskV2DTO updateTask(TaskV2 addTaskDTO, Integer taskId , Integer statusId){
//        final Integer finalStatusId;
//
//        if (addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be null");
//        }
//
//        StringBuilder errorMessage = new StringBuilder();
//        if (addTaskDTO.getTitle().length() > 100) {
//            errorMessage.append("Title size must be between 0 and 100. ");
//        }
//
//        if (addTaskDTO.getDescription() != null && addTaskDTO.getDescription().length() > 500) {
//            errorMessage.append("Description size must be between 0 and 500. ");
//        }
//
//        if (addTaskDTO.getAssignees() != null && addTaskDTO.getAssignees().length() > 30) {
//            errorMessage.append("Assignees size must be between 0 and 30. ");
//        }
//
//        if (errorMessage.length() > 0) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
//        }
//
//
//        if (statusId == null) {
//            finalStatusId = 1;
//        } else {
//            finalStatusId = statusId;
//        }
//
//        Status status = statusRepository.findById(finalStatusId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST
//                        , "Status ID " + finalStatusId + " Does Not EXIST!!!"));
//
//        if (addTaskDTO == null || addTaskDTO.getTitle() == null || addTaskDTO.getTitle().trim().isEmpty()) {
//            throw new IllegalArgumentException("TITLE IS REQUIRED!!!");
//        }
//
//        TaskV2 existingTask = repository.findById(taskId)
//                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "TASK ID " + taskId + " DOES NOT EXIST!!!"));
//
//        String id = existingTask.getId();
//        modelMapper.map(addTaskDTO, existingTask);
//        existingTask.setId(id);
//        existingTask.setStatus(status);
//        TaskV2 updatedTask = repository.saveAndFlush(existingTask);
//
//        return modelMapper.map(updatedTask, AddTaskV2DTO.class);
//    }
//}
