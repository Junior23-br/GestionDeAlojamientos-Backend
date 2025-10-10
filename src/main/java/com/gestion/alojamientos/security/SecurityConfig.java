package com.gestion.alojamientos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

     private final CustomAuthenticationEntryPoint customAuthEntryPoint;

    public SecurityConfig(CustomAuthenticationEntryPoint customAuthEntryPoint) {
        this.customAuthEntryPoint = customAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthEntryPoint)
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/signup",
                    "/api/v1/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // Todo lo demás requiere autenticación JWT
                .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * Proporciona un bean de PasswordEncoder usando BCrypt.
     *
     * @return Instancia de BCryptPasswordEncoder para encriptar contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
}