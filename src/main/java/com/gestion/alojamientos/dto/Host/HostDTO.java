package com.gestion.alojamientos.dto.Host;
import com.gestion.alojamientos.model.enums.StatesOfHost;

import java.util.Date;
import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la información principal de un anfitrión (Host).
 *
 * Este DTO se utiliza para transferir información del anfitrión entre las capas del sistema
 * sin exponer entidades del modelo ni datos sensibles (como contraseñas o relaciones completas).
 *
 * Contiene datos combinados de las clases SuperUser, NormalUser y Host.
 *
 * Evita incluir objetos de tipo entidad para prevenir problemas de serialización y
 * dependencias circulares en relaciones bidireccionales.
 */
public record HostDTO(

        /**
         * Identificador único del anfitrión
         */
        Long id,

        /**
         * Correo electrónico del anfitrión
         */
        String email,

        /**
         * Nombre de usuario (único en el sistema)
         */
        String username,

        /**
         * Nombre completo del anfitrión
         */
        String name,

        /**
         * Número de teléfono del anfitrión
         */
        String phoneNumber,

        /**
         * Fecha de nacimiento del anfitrión
         */
        Date birthDate,

        /**
         * URL de la foto de perfil del anfitrión
         */
        String urlProfilePhoto,

        /**
         * Estado actual del anfitrión (Activo, Inactivo, Suspendido, etc.)
         */
        StatesOfHost status,

        /**
         * Descripción personal del anfitrión
         */
        String personalDescription,

        /**
         * Lista de IDs de alojamientos asociados al anfitrión
         */
        List<Long> listAccommodationsIds,

        /**
         * Lista de IDs de comentarios que ha recibido el anfitrión
         */
        List<Long> hostCommentIds,

        /**
         * ID de la cuenta financiera asociada
         */
        Long financialAccountId,

        /**
         * ID de la tarifa de servicio asociada
         */
        Long serviceFeeId

) { }
