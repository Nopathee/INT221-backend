package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AddStatusDTO;
import com.example.int221backend.entities.Status;
import com.example.int221backend.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
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
    public ResponseEntity<Status> getStatusById(@PathVariable Integer id){
        Status status = service.getStatusById(id);
        return ResponseEntity.ok(status);
    }

    @PostMapping("")
    public ResponseEntity<?> createStatus(@RequestBody AddStatusDTO addStatusDTO) {
        try {
            if (addStatusDTO.getName() != null) {
                addStatusDTO.setName(addStatusDTO.getName().trim());
            }

            if (addStatusDTO.getDescription() != null) {
                addStatusDTO.setDescription(addStatusDTO.getDescription().trim());
            }

            String boardId = addStatusDTO.getBId();

            AddStatusDTO newStatus = service.addStatus(addStatusDTO, boardId);

            return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStatus(@PathVariable Integer id, @RequestBody Status addStatusDTO) {
            try {
                if (addStatusDTO.getName() != null) {
                    addStatusDTO.setName(addStatusDTO.getName().trim());
                }

                if (addStatusDTO.getDescription() != null) {
                    addStatusDTO.setDescription(addStatusDTO.getDescription().trim());
                }

                Status editedStatus = service.editStatus(addStatusDTO, id);
                return ResponseEntity.ok(editedStatus);
            }catch (ResponseStatusException e){
                return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
            }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable Integer id){
        try {
            service.deleteStatus(id);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @DeleteMapping("/{id}/{newId}")
    public ResponseEntity<?> deleteAndTranStatus(@PathVariable Integer id,@PathVariable Integer newId){
        try {
            service.deleteAndTranStatus(id, newId);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

}
