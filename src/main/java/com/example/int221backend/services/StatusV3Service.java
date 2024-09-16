package com.example.int221backend.services;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.repositories.local.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StatusV3Service {
    @Autowired
    private StatusV3Repository statusV3Repository;

    @Autowired
    private TaskV3Repository taskV3Repository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BoardService boardService;

    public List<Status> getAllStatus(String boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board ID " + boardId + " DOES NOT EXIST!!!"));

        return statusV3Repository.findByBoard(board);
    }

    public Status getStatusById(Integer id) {
        return statusV3Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));
    }

    @Transactional
    public AddStatusDTO addStatus(AddStatusDTO addStatusDTO, String boardId) {
        validateAddStatusDTO(addStatusDTO);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board ID " + boardId + " Does not exist!!!"));

        Status status = modelMapper.map(addStatusDTO, Status.class);
        status.setBoard(board);

        return modelMapper.map(statusV3Repository.save(status), AddStatusDTO.class);
    }

    private void validateAddStatusDTO(AddStatusDTO addStatusDTO) {
        if (addStatusDTO.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must not be null");
        }

        if (statusV3Repository.existsByName(addStatusDTO.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be unique");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (addStatusDTO.getName().length() > 50) {
            errorMessage.append("Name size must be between 0 and 50. ");
        }

        if (addStatusDTO.getDescription() != null && addStatusDTO.getDescription().length() > 200) {
            errorMessage.append("Description size must be between 0 and 200. ");
        }

        if (errorMessage.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }
    }

    @Transactional
    public Status editStatus(Status status, Integer statusId) {
        validateEditStatus(status, statusId);

        Status existingStatus = statusV3Repository.findById(statusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + statusId + " DOES NOT EXIST!!!"));

        if (existingStatus.getName().equals("No Status") || existingStatus.getName().equals("Done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, existingStatus.getName() + " Cannot Be changed");
        }

        existingStatus.setName(status.getName().trim());
        existingStatus.setDescription(status.getDescription() != null ? status.getDescription() : null);

        return statusV3Repository.save(existingStatus);
    }

    private void validateEditStatus(Status status, Integer statusId) {
        if (status.getName() == null || status.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must not be null");
        }

        if (statusV3Repository.existsByNameAndIdNot(status.getName(), statusId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be unique");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (status.getName().length() > 50) {
            errorMessage.append("Name size must be between 0 and 50. ");
        }

        if (status.getDescription() != null && status.getDescription().length() > 200) {
            errorMessage.append("Description size must be between 0 and 200. ");
        }

        if (errorMessage.length() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage.toString());
        }
    }

    @Transactional
    public void deleteStatus(Integer id) {
        Status status = statusV3Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));

        if (status.getName().equals("No Status") || status.getName().equals("Done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, status.getName() + " Cannot Be deleted");
        }

        if (!taskV3Repository.findByStatusId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status with existing tasks");
        }

        statusV3Repository.delete(status);
    }

    @Transactional
    public void deleteAndTranStatus(Integer id, Integer newId) {
        if (id.equals(newId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination status for task transfer must be different from current status");
        }

        Status status = statusV3Repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));

        Status newStatus = statusV3Repository.findById(newId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + newId + " DOES NOT EXIST!!!"));

        if (status.getName().equals("No Status") || status.getName().equals("Done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, status.getName() + " Cannot Be deleted");
        }

        if (newStatus.getName().equals("No Status") || newStatus.getName().equals("Done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, newStatus.getName() + " Cannot Be updated");
        }

        if (!taskV3Repository.findByStatusId(id).isEmpty()) {
            List<TaskV3> tasks = taskV3Repository.findByStatusId(id);
            tasks.forEach(task -> {
                task.setStatus(newStatus);
                taskV3Repository.save(task);
            });
        }

        statusV3Repository.delete(status);
    }
    @Transactional
    public boolean existsStatusInBoard(String boardId, Integer statusId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board ID " + boardId + " DOES NOT EXIST!!!"));

        return statusV3Repository.findByBoardAndId(board, statusId) != null;
    }

}
