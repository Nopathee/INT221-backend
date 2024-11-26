package com.example.int221backend.services;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.dtos.StatusAndTaskCDTO;
import com.example.int221backend.entities.local.Board;
import com.example.int221backend.entities.local.Status;
import com.example.int221backend.entities.local.TaskV3;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Transactional("projectManagementTransactionManager")
    public List<StatusAndTaskCDTO> getAllStatus(String boardId) {
        List<Status> allStatus = statusV3Repository.findAllStatus(boardId);
        List<Object[]> taskCountResults = taskV3Repository.countTasksByStatusForBoard(boardId);

        Map<Integer, Long> taskCountMap = new HashMap<>();
        for (Object[] result : taskCountResults) {
            Integer statusId = (Integer) result[0];
            Long count = (Long) result[1];
            taskCountMap.put(statusId, count);
        }

        List<StatusAndTaskCDTO> statusWithTaskCountList = new ArrayList<>();
        for (Status status : allStatus) {
            Long taskCount = taskCountMap.getOrDefault(status.getId(), 0L);
            statusWithTaskCountList.add(new StatusAndTaskCDTO(
                    status.getId(),
                    status.getName(),
                    status.getDescription(),
                    status.getColor(),
                    status.getBoard().getBoardId(),
                    taskCount.toString()
            ));
        }

        return statusWithTaskCountList;
    }

    @Transactional("projectManagementTransactionManager")
    public Status getStatusById(Integer id, String boardId) {
        Status status = statusV3Repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("status not found"));
        if (!status.getBoard().getBoardId().equals(boardId)){
            throw new ItemNotFoundException("this status not in this board");
        }
        return status;
    }

    @Transactional("projectManagementTransactionManager")
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

    @Transactional("projectManagementTransactionManager")
    public Status editStatus(Status status, Integer statusId) {
        validateEditStatus(status, statusId);

        Status existingStatus = statusV3Repository.findById(statusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + statusId + " DOES NOT EXIST!!!"));

        if (existingStatus.getName().equals("No Status") || existingStatus.getName().equals("Done")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, existingStatus.getName() + " Cannot Be changed");
        }

        existingStatus.setName(status.getName().trim());
        existingStatus.setDescription(status.getDescription() != null ? status.getDescription() : null);
        existingStatus.setColor(status.getColor() != null ? status.getColor() : "#fffff");

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

    @Transactional("projectManagementTransactionManager")
    public void deleteStatus(Integer id,String boardId) {

        Status status = statusV3Repository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));

        if (status.getName().equals("No Status") || status.getName().equals("Done")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, status.getName() + " Cannot Be deleted");
        }

        if (!taskV3Repository.findByStatusId(id).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status with existing tasks");
        }

        statusV3Repository.deleteByIdAndBoardId(id,boardId);
    }

    @Transactional("projectManagementTransactionManager")
    public void deleteAndTranStatus(Integer id, Integer newId,String boardId) {
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

        statusV3Repository.deleteByIdAndBoardId(status.getId(), boardId);
    }
    @Transactional("projectManagementTransactionManager")
    public boolean existsStatusInBoard(String boardId, Integer statusId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board ID " + boardId + " DOES NOT EXIST!!!"));

        return statusV3Repository.findByBoardAndId(board, statusId) != null;
    }

    public Status getDefaultStatus(String boardId){
        return statusV3Repository.findDefaultStatus(boardId,"No Status");
    }

}
