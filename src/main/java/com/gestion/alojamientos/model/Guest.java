package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.Enums.StatesOfHost;
import com.gestion.alojamientos.model.base.UserBasic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.mapstruct.Mapping;

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
public class Guest extends UserBasic {


    /**
     * Estados del Huesped: Activo, Eliminado, Suspendido, Inactive.
     * Descripcion: En src/main/java/com/gestion/alojamientos/model/Enums/StatusOfGuest se encuentra la descripcion de cada uno de los estados
     */
    @Column(name = "statusGuest", nullable = false, length = 20)
    @Comment("Estado actual del usuario: Activo, Inactivo, Suspendido, Eliminado")
    private StatesOfHost state;

}
