package com.gestion.alojamientos.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * Entidad que representa a un huésped en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'guest'.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "guest")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID interno único del administrados creado automáticamente por el sistema")
    private Long id;

    @Column(name = "email", nullable = false, length = 50)
    @Comment("Email identificador del adminisatrador")
    private String email;

    @Column(name = "password", nullable = false, length = 50)
    @Comment("password admin")
    private String password;



}

