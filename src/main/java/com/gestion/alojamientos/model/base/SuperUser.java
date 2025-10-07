package com.gestion.alojamientos.model.base;

import java.util.List;

import com.gestion.alojamientos.model.message.Chat;
import com.gestion.alojamientos.model.message.Notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class SuperUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier

    @Column(name = "email", nullable = false, length = 255)
    private String email; // Email

    @Column(name = "password", nullable = false, length = 255)
    private String password; // Password

    @Column(name = "username", nullable = false, length = 100)
    private String username; // Username
}
