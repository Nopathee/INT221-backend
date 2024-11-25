package com.example.int221backend.controllers;

import com.example.int221backend.dtos.AuthResponseDTO;
import com.example.int221backend.dtos.LoginUserDTO;
import com.example.int221backend.exception.ItemNotFoundException;
import com.example.int221backend.exception.NotCreatedException;
import com.example.int221backend.repositories.shared.UserRepository;
import com.example.int221backend.services.JwtService;
import com.example.int221backend.services.JwtUserDetailsService;
import com.example.int221backend.services.UserService;
import com.example.int221backend.entities.shared.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://ip23ssi3.sit.kmutt.ac.th", "http://intproj23.sit.kmutt.ac.th","https://intproj23.sit.kmutt.ac.th"})
@RestController
@RequestMapping("")
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

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginUserDTO jwtRequestUser) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequestUser.getUserName(), jwtRequestUser.getPassword())
            );

            UserDetails userDetails = userService.loadUserByUsername(jwtRequestUser.getUserName());

            User user = userRepository.findByUsername(userDetails.getUsername());
            if (user == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
            }

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            AuthResponseDTO authResponse = new AuthResponseDTO(token,refreshToken);

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException ("User Password is incorrect !");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Object> refreshToken(@RequestHeader(value = "Authorization", required = false) String reToken){
        try {
            if (reToken == null || !reToken.startsWith("Bearer ")){
                throw new NotCreatedException("Refresh token is invalid!");
            }

            String token = reToken.substring(7);

            Claims claims = jwtService.getAllClaimsFromToken(token);

            if (claims.getExpiration().before(new Date())){
                throw new NotCreatedException("Refresh token has expired!");
            }

            String oid = claims.get("oid", String.class);
            User user = jwtUserDetailsService.getUserByOid(oid);

            if (user == null){
                throw new ItemNotFoundException("User not found");
            }

            String accessToken = jwtService.generateTokenWithClaims(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("access_token", accessToken);

            return ResponseEntity.ok(responseBody);
        } catch (ExpiredJwtException e) {
            throw new NotCreatedException("Refresh token has expired.");
        } catch (JwtException e) {
            throw new NotCreatedException("Invalid refresh token.");
        } catch (Exception e) {
            throw new NotCreatedException("Refresh token failed!");
        }
    }



}

