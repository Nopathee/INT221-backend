package com.example.int221backend.services;

import com.example.int221backend.entities.Status;
import com.example.int221backend.entities.Task;
import com.example.int221backend.repositories.StatusRepository;
import com.example.int221backend.repositories.TaskRepository;
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
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Status> getAllStatus(){
        return repository.findAll();
    }

    @Transactional
    public Status addStatus(Status status) {
        return repository.save(status);
    }

    @Transactional
    public Status editStatus(Status status, String statusId) {
        if (statusId == null || status.getName() == null || status.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is REQUIRED!!!");
        }
        Status existingStatus = repository.findById(statusId).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Status ID " + statusId + " DOES NOT EXIST!!!"));

        String id = existingStatus.getId();
        modelMapper.map(status, existingStatus);
        existingStatus.setId(id);
        Status editedStatus = repository.saveAndFlush(existingStatus);

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

        List<Task> tasks = taskRepository.findByStatusId(id);

        Status newStatus = repository.findById(newId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NEW STATUS ID " + newId + " DOES NOT EXIST!!!"));

        for (Task task : tasks){
            task.setStatus(newStatus);
        }

        taskRepository.saveAll(tasks);

        repository.delete(status);
    }
}
