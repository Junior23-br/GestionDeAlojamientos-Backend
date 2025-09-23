package com.gestion.alojamientos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(
            User.withUsername("admin")
                .password("{noop}admin123") // {noop} indica sin codificar
                .roles("ADMIN")
                .build()
        );
        manager.createUser(
            User.withUsername("user")
                .password("{noop}user123")
                .roles("USER")
                .build()
        );
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll() // Permite acceso libre a Swagger
                .anyRequest().authenticated()
            )
            .formLogin();
        return http.build();
    }
}