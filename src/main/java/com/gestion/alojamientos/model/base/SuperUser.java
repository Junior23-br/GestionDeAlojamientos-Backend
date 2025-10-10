package com.gestion.alojamientos.model.base;

import java.util.List;

import com.gestion.alojamientos.model.common.ResetCode;
import com.gestion.alojamientos.model.message.Chat;
import com.gestion.alojamientos.model.message.Notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class SuperUser  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID interno único de la persona creada automáticamente por el sistema")
    @Column(name = "id")
    private Long id; // Unique identifier

    @Column(name = "email", nullable = false, length = 255, unique = true)
    @Comment("Dirección de correo electrónico única para autenticación.")
    private String email; // Email

    @Column(name = "password", nullable = false, length = 255)
    @Comment("Contraseña segura de la persona y cifrada para autenticación")
    private String password; // Password

    @Column(name = "username", nullable = false, length = 100)
    private String username; // Username

    @Embedded
    private ResetCode resetCode;
}
