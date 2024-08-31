package com.example.int221backend.Filters;

import com.example.int221backend.user_services.JwtService;
import com.example.int221backend.user_services.UserService;
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
        final String requestTokenHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null) {
            if (requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                if (jwtToken == null || !jwtToken.matches("[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+")) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is not well-formed");
                }
                try {
                    username = jwtService.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is invalid");
                } catch (ExpiredJwtException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token has expired");
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is invalid");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT Token does not begin with Bearer String");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No JWT Token provided");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            if (jwtService.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is not valid");
            }
        }

        filterChain.doFilter(request, response);
    }
}

