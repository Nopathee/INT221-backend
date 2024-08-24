package com.example.int221backend.user_services;

import com.example.int221backend.authen.AuthUser;
import com.example.int221backend.exception.NotCreatedException;
import com.example.int221backend.user_entities.UserRepository;
import com.example.int221backend.user_entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


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

    public User getUserByUserName(String username){
        User user = userRepository.findByUsername(username);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null ){
            throw new NotCreatedException("Username or Password is incorrect");
        }
        UserDetails userDetails = new AuthUser(username,user.getPassword());
        return userDetails;
    }
}
