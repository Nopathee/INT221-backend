package com.example.int221backend.services;

import com.example.int221backend.entities.shared.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService implements Serializable {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("#{${jwt.max-token-interval-hour}*60*60*1000}")
    private int JWT_TOKEN_VALIDITY;

    @Value("#{${jwt.refresh-token-interval-hour}*60*60*1000}")
    private int JWT_REFRESH_TOKEN_VALIDITY;

    // Extract username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getOidFromToken(String token){
        return getClaimFromToken(token, claims -> claims.get("oid", String.class));
    }

    // Extract expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Extract specific claims from token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generate a new token
    public String generateToken(User userInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userInfo.getUsername());
        claims.put("name", userInfo.getName());
        claims.put("oid", userInfo.getOid());
        claims.put("email", userInfo.getEmail());
        claims.put("role", userInfo.getRole());
        return doGenerateToken(claims);
    }

    // Create the token with claims and subject
    private String doGenerateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
//                .setSubject(subject)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ssi3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(User userInfo){
        Map<String, Object> claims = new HashMap<>();
        claims.put("oid", userInfo.getOid());

        return doGenerateRefreshToken(claims);
    }

    public String doGenerateRefreshToken(Map<String, Object> claims){
        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ssi3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
    }

    // Validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateTokenWithClaims(User userInfo) {
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("sub", userInfo.getUsername());
        tokenClaims.put("oid", userInfo.getOid());
        tokenClaims.put("name", userInfo.getName());
        tokenClaims.put("email", userInfo.getEmail());
        tokenClaims.put("role", userInfo.getRole());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(tokenClaims)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ssi3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
    }

}
