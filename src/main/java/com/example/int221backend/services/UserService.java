package com.example.int221backend.services;

import com.example.int221backend.entities.local.UserLocal;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.repositories.local.UserLocalRepository;
import com.example.int221backend.repositories.shared.UserRepository;
import com.example.int221backend.entities.shared.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserLocalRepository userLocalRepository;

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

    public User getUserByUserName(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.trim().toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("Username or Password is incorrect");
        }

        UserLocal userLocal = new UserLocal();
        userLocal.setOid(user.getOid());
        userLocal.setUsername(user.getUsername());
        userLocal.setName(user.getName());
        userLocal.setEmail(user.getEmail());
        userLocal.setRole(user.getRole());

        userLocalRepository.save(userLocal);

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public UserLocal findByOid(String oid) {
        return userLocalRepository.findByOid(oid).orElseThrow(() -> new ItemNotFoundException("user not found"));
    }





}
