package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AuthResponseDTO;
import com.example.int221backend.dtos.LoginUserDTO;
import com.example.int221backend.exception.NotCreatedException;
import com.example.int221backend.repositories.shared.UserRepository;
import com.example.int221backend.services.JwtService;
import com.example.int221backend.services.UserService;
import com.example.int221backend.entities.shared.User;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173","http://ip23ssi3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginUserDTO jwtRequestUser) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword())
            );

            UserDetails userDetails = userService.loadUserByUsername(jwtRequestUser.getUserName());
            String token = jwtService.generateToken(userRepository.findByUsername(userDetails.getUsername()));
            String refreshToken = jwtService.generateRefreshToken(userRepository.findByUsername(userDetails.getUsername()));
            AuthResponseDTO authResponse = new AuthResponseDTO(token,refreshToken);

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException ("User Password is incorrect !");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Object> refreshToken(@RequestHeader("Authorization") String token){
        try {
            Claims claims = jwtService.getAllClaimsFromToken(token);
            String accessToken = jwtService.generateTokenWithClaims(claims);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", accessToken);

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            throw new NotCreatedException("Refresh token failed!");
        }
    }



}

