package com.example.int221backend.controllers;

import com.example.int221backend.entities.Status;
import com.example.int221backend.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@RequestMapping("v2/statuses")
public class StatusController {

    @Autowired
    private StatusService service;

    @GetMapping("")
    public List<Status> getAllStatus(){
        return service.getAllStatus();
    }

    @PostMapping("")
    public Status createStatus(@RequestBody Status status) {
        return service.addStatus(status);
    }

    @PutMapping("/{id}")
    public Status editStatus(@PathVariable String id, @RequestBody Status status) {
        return service.editStatus(status, id);

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
