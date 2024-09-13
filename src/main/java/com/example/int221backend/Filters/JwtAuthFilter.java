package com.example.int221backend.Filters;

import com.example.int221backend.services.JwtService;
import com.example.int221backend.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);

            if (!isValidJwtStructure(jwtToken)) {
                System.out.println("Invalid JWT token structure");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JWT token structure");
            }

            try {
                username = jwtService.getUsernameFromToken(jwtToken);
                System.out.println("Username from token: " + username);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token is expired");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT token is expired", e);
            } catch (Exception e) {
                System.out.println("Invalid JWT token");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JWT token", e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                if (jwtService.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication set in SecurityContext");
                } else {
                    System.out.println("JWT token is not valid");
                }
            } catch (UsernameNotFoundException e) {
                System.out.println("User not found: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found", e);
            }
        }

            filterChain.doFilter(request, response);
    }

    private boolean isValidJwtStructure(String jwtToken) {
        return jwtToken.matches("[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+");
    }
}
