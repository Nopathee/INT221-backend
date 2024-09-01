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
import java.util.Arrays;
import java.util.List;


    @Component
    public class JwtAuthFilter extends OncePerRequestFilter {
        @Autowired
        private UserService userService;
        @Autowired
        private JwtService jwtService;
        private static final List<String> EXCLUDED_PATHS = Arrays.asList("/login", "/register", "/decode-token", "/v2/api-docs");

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if (isExcludedPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwtToken = extractJwtFromRequest(request);

            if (jwtToken != null) {
                try {
                    processJwtAuthentication(request, jwtToken);
                } catch (ExpiredJwtException e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token has expired", e);
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is invalid", e);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No JWT Token provided");
            }

            filterChain.doFilter(request, response);
        }

        private boolean isExcludedPath(HttpServletRequest request) {
            return EXCLUDED_PATHS.contains(request.getServletPath());
        }

        private String extractJwtFromRequest(HttpServletRequest request) {
            final String requestTokenHeader = request.getHeader("Authorization");

            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                String jwtToken = requestTokenHeader.substring(7);
                if (isValidJwtStructure(jwtToken)) {
                    return jwtToken;
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is not well-formed");
                }
            } else if (requestTokenHeader != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JWT Token does not begin with Bearer String");
            }
            return null;
        }

        private void processJwtAuthentication(HttpServletRequest request, String jwtToken) {
            String username = jwtService.getUsernameFromToken(jwtToken);
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.validateToken(jwtToken, userDetails)) {
                setAuthenticationContext(request, userDetails);
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is not valid");
            }
        }

        private boolean isValidJwtStructure(String jwtToken) {
            return jwtToken.matches("[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+");
        }

        private void setAuthenticationContext(HttpServletRequest request, UserDetails userDetails) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

