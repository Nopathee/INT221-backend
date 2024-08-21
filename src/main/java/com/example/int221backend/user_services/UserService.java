package com.example.int221backend.user_services;

import com.example.int221backend.user_entities.UserRepository;
import com.example.int221backend.user_entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
    public String getFullnameByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getName(); // หรือโปรดเปลี่ยนให้ตรงกับการตั้งชื่อที่ใช้ใน User entity ของคุณ
        }
        throw new RuntimeException("User not found");
    }



}
