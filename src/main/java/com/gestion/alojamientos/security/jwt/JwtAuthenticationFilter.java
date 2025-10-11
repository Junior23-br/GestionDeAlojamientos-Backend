package com.gestion.alojamientos.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header: " + authHeader); // Debugging line
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Extracted Token: " + token); // Debugging line
            System.out.println("Is Token Valid: " + jwtUtil.validateToken(token)); // Debugging line
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                System.out.println("Username from Token: " + username); // Debugging line
                System.out.println("Role from Token: " + role); // Debugging line

                 // Sin agregar "ROLE_" manualmente
                List<SimpleGrantedAuthority> authorities = role != null ?
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)) : Collections.emptyList();

                System.out.println("Authorities: " + authorities); // Debugging line
                
                // // Spring espera roles con prefijo ROLE_
                // String roleName = role != null ? "ROLE_" + role : null;
                // List<SimpleGrantedAuthority> authorities = roleName != null ?
                //         List.of(new SimpleGrantedAuthority(roleName)) : Collections.emptyList();

                UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
