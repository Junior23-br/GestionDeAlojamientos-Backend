package com.gestion.alojamientos.model.users;

import com.gestion.alojamientos.model.enums.StatesAdmin;
import jakarta.persistence.*;
import lombok.*;

import com.gestion.alojamientos.model.base.SuperUser;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa a un huésped en el sistema.
 * Extiende de SuperUser y se mapea a la tabla 'Admin'.
 */
@Getter
@Setter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin")
@EqualsAndHashCode(callSuper = true)
public class Admin extends SuperUser {
    
    @Column(name = "acces_level", nullable = false)
    private int acces_level;
    @Column(name = "statesAdmin", nullable = false)
    private StatesAdmin statesAdmin;

}
