package com.vijay.demo.util;

import com.vijay.demo.configuration.UserInfoUserDetailsService;
import com.vijay.demo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    // This method intercepts incoming HTTP requests and validates the JWT token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // Get the Authorization header from the request
        String token = null;
        String username = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Extract the token (substring of the header after "Bearer ")
            username = jwtService.extractUsername(token); // Extract the username from the token
        }

        // If the username is not null and the SecurityContext doesn't already contain authentication, proceed with validation
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details based on the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token using JwtService
            if (jwtService.validateToken(token, userDetails)) {
                // If valid, create an authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set the authentication details (including request information) in the authentication token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue the filter chain (pass the request and response to the next filter in the chain)
        filterChain.doFilter(request, response);
    }
}
