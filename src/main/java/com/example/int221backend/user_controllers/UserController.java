package com.example.int221backend.user_controllers;

import com.example.int221backend.user_dtos.LoginUserDTO;
import com.example.int221backend.user_services.UserService;
import com.example.int221backend.user_entities.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user")
    public ResponseEntity<Void> loginUser(@RequestBody LoginUserDTO loginUserDTO) {
        boolean isValidUser = userService.validateUser(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        if (isValidUser) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
