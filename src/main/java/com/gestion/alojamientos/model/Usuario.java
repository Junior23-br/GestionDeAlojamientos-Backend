package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.base.Persona;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Entidad que representa a un usuario en el sistema.
 * Extiende de Persona y se mapea a la tabla 'usuario'.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario extends Persona {

}
