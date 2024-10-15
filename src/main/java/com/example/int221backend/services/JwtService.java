package com.example.int221backend.services;

import com.example.int221backend.entities.shared.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.lang.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService implements Serializable {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("#{60*30*1000}")
    private long JWT_TOKEN_VALIDITY;

    @Value("#{24*60*60*1000}")
    private long JWT_REFRESH_TOKEN_VALIDITY;

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
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
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

    public String generateRefreshToken(User userInfo){
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userInfo.getName());
        claims.put("oid", userInfo.getOid());
        claims.put("email", userInfo.getEmail());
        claims.put("role", userInfo.getRole());
        return doGenerateRefreshToken(claims, userInfo.getUsername());
    }

    public String doGenerateRefreshToken(Map<String, Object> claims, String subject ){
        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://intproj23.sit.kmutt.ac.th/ssi3/")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_TOKEN_VALIDITY))
                .signWith(signatureAlgorithm, SECRET_KEY)
                .compact();
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

    // Validate the token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateTokenWithClaims(Claims claims) {
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("sub", claims.getSubject());
        tokenClaims.put("oid", claims.get("oid"));
        tokenClaims.put("iss", claims.getIssuer());
        tokenClaims.put("email", claims.get("email"));
        return doGenerateToken(tokenClaims);
    }

}
