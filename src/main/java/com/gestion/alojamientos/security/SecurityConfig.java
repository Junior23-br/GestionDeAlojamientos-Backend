package com.gestion.alojamientos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para la aplicación utilizando Spring Security.
 * Define la autenticación y autorización, incluyendo el codificador de contraseñas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     *
     * @param http Objeto para configurar las reglas de seguridad HTTP.
     * @return Cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/guests/**").permitAll() // Permitir acceso público a endpoints de usuarios por ahora
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // Usar la nueva clase
                .and();
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
}