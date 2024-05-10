package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th"})
@RestController
@RequestMapping("v2/statuses")
public class StatusController {

    @Autowired
    private StatusService service;

    @GetMapping("")
    public List<Status> getAllStatus(){
        return service.getAllStatus();
    }

    @GetMapping("/{id}")
    public Status getStatusById(@PathVariable String id){
        return service.getStatusById(id);
    }

    @PostMapping("")
    public ResponseEntity<AddStatusDTO> createStatus(@RequestBody AddStatusDTO addStatusDTO) {
        AddStatusDTO newStatus = service.addStatus(addStatusDTO);

        return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddStatusDTO> editStatus(@PathVariable String id, @RequestBody AddStatusDTO addStatusDTO) {
        try {
            AddStatusDTO editedStatus = service.editStatus(addStatusDTO,id);

            return ResponseEntity.ok(editedStatus);
        }catch (HttpClientErrorException e){
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteStatus(@PathVariable String id){
        service.deleteStatus(id);
    }

    @DeleteMapping("/{id}/{newId}")
    public void deleteAndTranStatus(@PathVariable String id,@PathVariable String newId){
        service.deleteAndTranStatus(id,newId);
    }

}
