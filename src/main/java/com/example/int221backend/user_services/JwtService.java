package com.example.int221backend.user_services;

import com.example.int221backend.user_entities.User;
import com.example.int221backend.user_entities.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("oid", user.getOid());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ft/")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String getFullnameByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getName() : null;
    }

    private String getUserOid(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getOid() : null;
    }

    private String getUserEmail(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getEmail() : null;
    }

    private String getUserRole(String username) {
        User user = userRepository.findByUsername(username);
        return user != null ? user.getRole() : null;
    }

    public String generateTokenForUser(String username) {
        return Jwts.builder()
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ft/")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 นาที
                .claim("name", getFullnameByUsername(username))
                .claim("oid", getUserOid(username))
                .claim("email", getUserEmail(username))
                .claim("role", getUserRole(username))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
