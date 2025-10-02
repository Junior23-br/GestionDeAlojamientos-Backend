package com.gestion.alojamientos.model.users;

import jakarta.persistence.*;
import lombok.*;

import com.gestion.alojamientos.model.base.SuperUser;

/**
 * Entidad que representa a un huésped en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'guest'.
 */
@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin")
public class Admin extends SuperUser {
    
    @Column(name = "acces_level", nullable = false)
    private int acces_level;

}
