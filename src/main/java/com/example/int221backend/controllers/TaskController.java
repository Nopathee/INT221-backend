//package com.example.int221backend.controllers;
//
//import com.example.int221backend.dtos.AddTaskDTO;
//import com.example.int221backend.dtos.AddTaskV1DTO;
//import com.example.int221backend.dtos.SimpleTaskDTO;
//import com.example.int221backend.entities.Task;
//import com.example.int221backend.entities.TaskStatus;
//import com.example.int221backend.services.TaskService;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
//@RestController
//@RequestMapping("v1/tasks")
//public class TaskController {
//    @Autowired
//    private TaskService service;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
////    @GetMapping("")
////    public ResponseEntity<Object> getAllTask(){
////        List<Task> task = service.getAllTask();
////        SimpleTaskDTO simpleTask = modelMapper.map(task, SimpleTaskDTO.class);
////        return ResponseEntity.ok(simpleTask);
////    }
//
//    @GetMapping("")
//    public ResponseEntity<Object> getAllTask(){
//        List<Task> tasks = service.getAllTask();
//        List<SimpleTaskDTO> simpleTaskDTOs = tasks.stream()
//                .map(task -> modelMapper.map(task, SimpleTaskDTO.class))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(simpleTaskDTOs);
//    }
//
//    @GetMapping("/{id}")
//    public Task getTaskById(@PathVariable Integer id){
//        return service.getTaskById(id);
//    }
//
//    @PostMapping("")
//    public ResponseEntity<AddTaskV1DTO> addTask(@RequestBody AddTaskV1DTO addTaskDTO) {
//
//        if (addTaskDTO.getStatus() == null) {
//            addTaskDTO.setStatus(TaskStatus.NO_STATUS);
//        }
//
//        if (addTaskDTO.getTitle() != null) {
//            addTaskDTO.setTitle(addTaskDTO.getTitle().trim());
//        }
//
//        if (addTaskDTO.getDescription() != null){
//            addTaskDTO.setDescription(addTaskDTO.getDescription().trim());
//        }
//
//        if (addTaskDTO.getAssignees() != null){
//            addTaskDTO.setAssignees(addTaskDTO.getAssignees().trim());
//        }
//
//        AddTaskV1DTO newTask = service.addTask(addTaskDTO);
//
//        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
//    }
//
//
//    @DeleteMapping("/{id}")
//    public void deleteTask(@PathVariable Integer id){
//        service.deleteTask(id);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<AddTaskV1DTO> updateTask(@PathVariable Integer id, @RequestBody AddTaskV1DTO task){
//        try {
//            if (task.getTitle() != null) {
//                task.setTitle(task.getTitle().trim());
//            }
//
//            if (task.getDescription() != null){
//                task.setDescription(task.getDescription().trim());
//            }
//
//            if (task.getAssignees() != null){
//                task.setAssignees(task.getAssignees().trim());
//            }
//
//            AddTaskV1DTO updatedTask = service.updateTask(task,id);
//
//            return ResponseEntity.ok(updatedTask);
//        }catch (HttpClientErrorException e){
//            return ResponseEntity.status(e.getStatusCode()).body(null);
//        }
//    }
//}