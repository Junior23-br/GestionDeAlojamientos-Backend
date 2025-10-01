package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.base.UserBasic;
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
public class Guest extends UserBasic {
    /**
     * Estados del Huesped: Activo, Eliminado, Suspendido, Inactive.
     * Descripcion: En src/main/java/com/gestion/alojamientos/model/Enums/StatusOfGuest se encuentra la descripcion de cada uno de los estados
     */
    @Enumerated(EnumType.STRING)
    private StatesOfGuest state;


}
