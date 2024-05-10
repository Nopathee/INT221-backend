package com.example.int221backend.services;

import com.example.int221backend.dtos.AddStatusDTO;
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
public class StatusService {
    @Autowired
    private StatusRepository repository;

    @Autowired
    private TaskV2Repository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus(){
        return repository.findAll();
    }

    public Status getStatusById(String id){
        return repository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));
    }

    @Transactional
    public AddStatusDTO addStatus(AddStatusDTO addStatusDTO) {
        Status status = modelMapper.map(addStatusDTO, Status.class);
        return modelMapper.map(repository.saveAndFlush(status), addStatusDTO.getClass() );
    }

    @Transactional
    public Status editStatus(Status status, String statusId) {
        if (statusId == null || status.getName() == null || status.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is REQUIRED!!!");
        }
        Status existingStatus = repository.findById(statusId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Status ID " + statusId + " DOES NOT EXIST!!!"));

        String id = existingStatus.getId();
        modelMapper.map(status, existingStatus);
        existingStatus.setId(id);
        Status editedStatus = repository.saveAndFlush(existingStatus);
//        AddStatusDTO editedStatusDTO = modelMapper.map(editedStatus,AddStatusDTO.class);

        return editedStatus;
    }

    @Transactional
    public void deleteStatus(String id) {
        Status status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STATUS ID " + id + " DOES NOT EXIST!!!"));
        repository.delete(status);
    }

    @Transactional
    public void deleteAndTranStatus(String id, String newId){
        Status status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"STATUS ID " + id + " DOES NOT EXIST!!!"));

        List<TaskV2> tasks = taskRepository.findByStatusId(id);

        Status newStatus = repository.findById(newId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NEW STATUS ID " + newId + " DOES NOT EXIST!!!"));

        for (TaskV2 task : tasks){
            task.setStatus(newStatus);
        }

        taskRepository.saveAll(tasks);

        repository.delete(status);
    }
}
