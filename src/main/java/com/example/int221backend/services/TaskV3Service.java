package com.example.int221backend.services;

import com.example.int221backend.dtos.AddTaskDTO;
import com.example.int221backend.dtos.AddTaskV2DTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.entities.local.TaskV3; // เปลี่ยนเป็น TaskV3
import com.example.int221backend.exception.BadRequestException;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.BoardRepository;
import com.example.int221backend.repositories.local.StatusRepository;
import com.example.int221backend.repositories.local.TaskV3Repository; // เปลี่ยนเป็น TaskV3Repository
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;
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
    private StatusV3Service statusV3Service;

    @Autowired
    private BoardRepository boardRepository;

    @Transactional("projectManagementTransactionManager")
    public List<TaskV3> getAllTask(Set<String> filterStatuses,String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board id" + boardId + "does not exist"));

        if (filterStatuses == null || filterStatuses.isEmpty()) {
            return taskV3Repository.findByBoard_BoardId(boardId);
        } else {
            return taskV3Repository.findAll().stream()
                    .filter(task -> filterStatuses.contains(task.getStatus().getName()))
                    .collect(Collectors.toList());
        }
    }

    @Transactional("projectManagementTransactionManager")
    public TaskV3 getTaskById(String id, String boardId) {
        System.out.println(id);
        TaskV3 taskV3 = taskV3Repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Task not found"));

        System.out.println(taskV3);

        if (!taskV3.getBoard().getBoardId().equals(boardId)){
            throw new ItemNotFoundException("Board not found!");
        }

        return taskV3;
    }

    @Transactional("projectManagementTransactionManager")
    public AddTaskV2DTO addTask(AddTaskDTO addTaskV2DTO, Integer statusId, String boardId) {

        if (addTaskV2DTO.getTitle() == null || addTaskV2DTO.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title must not be null");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (addTaskV2DTO.getTitle().length() > 100) {
            errorMessage.append("Title size must be between 0 and 100. ");
        }

        if (addTaskV2DTO.getDescription() != null && addTaskV2DTO.getDescription().length() > 500) {
            errorMessage.append("Description size must be between 0 and 500. ");
        }

        if (addTaskV2DTO.getAssignees() != null && addTaskV2DTO.getAssignees().length() > 30) {
            errorMessage.append("Assignees size must be between 0 and 30. ");
        }

        if (!errorMessage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }

        final Integer finalStatusId = (statusId == null) ? statusV3Service.getDefaultStatus(boardId).getId() : statusId;
        Status status = statusV3Service.getStatusById(finalStatusId,boardId);
        if (status == null){
            throw new ItemNotFoundException("this status id not found!");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board ID " + boardId + " does not exist"));

        TaskV3 task = modelMapper.map(addTaskV2DTO, TaskV3.class);
        task.setStatus(status);
        task.setBoard(board);

        AddTaskV2DTO taskV2DTO = modelMapper.map(task, AddTaskV2DTO.class);

        return modelMapper.map(taskV3Repository.saveAndFlush(task), taskV2DTO.getClass());
    }

    @Transactional("projectManagementTransactionManager")
    public void deleteTask(String taskId, String boardId){
        TaskV3 task = taskV3Repository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TASK ID" + taskId + "DOES NOT EXiTS!!!"));
        if (!task.getBoard().getBoardId().equals(boardId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found in board");
        }

        taskV3Repository.delete(task);
    }

    @Transactional("projectManagementTransactionManager")
    public AddTaskV2DTO updateTask(TaskV3 addTaskDTO, String taskId, Integer statusId, String boardId) {
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
            errorMessage.append("Assignees size must be between 0 and 30.");
        }

        if (errorMessage.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }

        final Integer finalStatusId = statusId == null ? 1 : statusId;

        Status status = statusV3Service.getStatusById(finalStatusId,boardId);
        if (status == null){
            throw new ItemNotFoundException("this status is not found");
        }

        TaskV3 existingTask = taskV3Repository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found @ service"));

        if (!existingTask.getBoard().getBoardId().equals(boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found in the specified board.");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board ID " + boardId + " does not exist"));

        String id = existingTask.getId();
        modelMapper.map(addTaskDTO, existingTask);
        existingTask.setId(id);
        existingTask.setStatus(status);
        existingTask.setBoard(board);

        TaskV3 updatedTask = taskV3Repository.saveAndFlush(existingTask);
        return modelMapper.map(updatedTask, AddTaskV2DTO.class);
    }

    public boolean existingTask(String taskId,String boardId){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ItemNotFoundException("Board not found!"));

        return taskV3Repository.findByBoardAndId(board,taskId) != null;
    }

}
