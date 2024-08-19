package com.example.int221backend.user_services;

import com.example.int221backend.user_entities.UserRepository;
import com.example.int221backend.user_entities.User;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public List<User> getAllUsers() {return userRepository.findAll();}
}
