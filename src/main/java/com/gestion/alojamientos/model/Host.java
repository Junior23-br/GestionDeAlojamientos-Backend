package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.Enums.StatesOfHost;
import com.gestion.alojamientos.model.base.UserBasic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * Entidad que representa a un anfitrión en el sistema.
 * Extiende de UserBasic y se mapea a la tabla 'anfitrion'.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "host")
public class Host extends UserBasic {


    /**
     * Estados del Anfitrion: Activo, Inactivo, Suspendido, Eliminado, Pendiente, Aprovado, Rechazado.
     * Descripcion: En src/main/java/com/gestion/alojamientos/model/Enums/StatusOfHost se encuentra la descripcion de cada uno de los estados
     */
    @Column(name = "statusHost", nullable = false, length = 20)
    @Comment("Estado actual del usuario: Activo, Inactivo, Suspendido, Eliminado" + "\n" + " Pendiente, Aprovado, Rechazado.")
    private StatesOfHost status;





}