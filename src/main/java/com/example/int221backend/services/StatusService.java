package com.example.int221backend.services;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.TaskV2;
import com.example.int221backend.repositories.StatusRepository;
import com.example.int221backend.repositories.TaskV2Repository;
import com.example.int221backend.entities.Board;
import com.example.int221backend.repositories.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class   StatusService {
    @Autowired
    private StatusRepository repository;

    @Autowired
    private TaskV2Repository taskRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus(){
        return repository.findAll();
    }

    public Status getStatusById(Integer id){
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));
    }

    @Transactional
    public AddStatusDTO addStatus(AddStatusDTO addStatusDTO, String boardId) {
        if (addStatusDTO.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name must not be null");
        }

        if (repository.existsByName(addStatusDTO.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be unique");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (addStatusDTO.getName().length() > 50){
            errorMessage.append("Name size must be between 0 and 50. ");
        }

        if (addStatusDTO.getDescription() != null && addStatusDTO.getDescription().length() > 200){
            errorMessage.append("Description size must be between 0 and 200. ");
        }

        if (errorMessage.length() > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage.toString());
        }

        if (repository.existsByName(addStatusDTO.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Status name must be unique");
        }
        Board board = boardRepository.findById(addStatusDTO.getBId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Board ID " + addStatusDTO.getBId() + "Does not exist!!!"));

        Status status = modelMapper.map(addStatusDTO, Status.class);
        status.setBoard(board);

        return modelMapper.map(repository.save(status), AddStatusDTO.class);
    }

    @Transactional
    public Status editStatus(Status status, Integer statusId) {
        if (status.getName() == null || status.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name must not be null");
        }

        if (repository.existsByNameAndIdNot(status.getName(),statusId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be unique");
        }

        StringBuilder errorMessage = new StringBuilder();
        if (status.getName().length() > 50){
            errorMessage.append("Name size must be between 0 and 50. ");
        }

        if (status.getDescription() != null && status.getDescription().length() > 200){
            errorMessage.append("Description size must be between 0 and 200. ");
        }

        if (!errorMessage.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage.toString());
        }



        Status existingStatus = repository.findById(statusId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status ID " + statusId + " DOES NOT EXIST!!!"));
        if (existingStatus.getName().equals("No Status")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Status Cannot Be changed");
        }

        if (existingStatus.getName().equals("Done")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Done Cannot Be changed");
        }

        if (repository.existsByNameAndIdNot(status.getName(), statusId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Status name must be unique");
        }

        //existingStatus.setName(status.getName().trim());
        //existingStatus.setDescription(status.getDescription() != null ? status.getDescription() : null);
        //existingStatus.setColor(status.getColor() != null ? status.getColor().trim() : null);
        return repository.save(existingStatus);
    }

    @Transactional
    public void deleteStatus(Integer id) {
        Status status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));

        if (status.getName().equals("No Status")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Status Cannot Be deleted");
        }

        if (status.getName().equals("Done")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Done Cannot Be deleted");
        }

        if (!taskRepository.findByStatusId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete status with existing tasks");
        }

        repository.delete(status);
    }

    @Transactional
    public void deleteAndTranStatus(Integer id, Integer newId){
        if (id.equals(newId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "destination status for task transfer must be different from current statust");
        }

        Status status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));

        Status newStatus = repository.findById(newId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "the specified status for task transfer does not exist"));

        List<TaskV2> tasks = taskRepository.findByStatusId(id);
        if (tasks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "To-delete status does not have any task to transfer");
        }

        for (TaskV2 task : tasks) {
            task.setStatus(newStatus);
        }
        taskRepository.saveAll(tasks);
        repository.delete(status);
    }
}
