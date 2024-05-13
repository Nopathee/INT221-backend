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
    public ResponseEntity<AddStatusDTO> createStatus(@RequestBody AddStatusDTO addStatusDTO) {

        if (addStatusDTO.getName() != null){
            addStatusDTO.setName(addStatusDTO.getName().trim());
        }

        if (addStatusDTO.getDescription() != null){
            addStatusDTO.setDescription(addStatusDTO.getDescription().trim());
        }

        AddStatusDTO newStatus = service.addStatus(addStatusDTO);

        return new ResponseEntity<>(newStatus, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Status> editStatus(@PathVariable Integer id, @RequestBody Status addStatusDTO) {
        try {

            if (addStatusDTO.getName() != null){
                addStatusDTO.setName(addStatusDTO.getName().trim());
            }

            if (addStatusDTO.getDescription() != null){
                addStatusDTO.setDescription(addStatusDTO.getDescription().trim());
            }

            if (id == 1) {
//                Status oldStatus = service.getStatusById(id);
//                return ResponseEntity.ok(oldStatus);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else {
                Status editedStatus = service.editStatus(addStatusDTO, id);
                return ResponseEntity.ok(editedStatus);
            }
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteStatus(@PathVariable Integer id){
        service.deleteStatus(id);
    }

    @DeleteMapping("/{id}/{newId}")
    public void deleteAndTranStatus(@PathVariable Integer id,@PathVariable Integer newId){
        service.deleteAndTranStatus(id,newId);
    }

}
