package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.base.UserBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa a un anfitrión en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'anfitrion'.
 */
@Getter
@Setter
@Entity
@Table(name = "host")
public class Host extends UserBasic {
}