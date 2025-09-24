package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.base.UserBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa a un huésped en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'guest'.
 */
@Getter
@Setter
@Entity
@Table(name = "guest")
public class Guest extends UserBasic {
}
