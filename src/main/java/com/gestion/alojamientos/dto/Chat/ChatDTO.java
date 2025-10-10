package com.gestion.alojamientos.dto.Chat;

import java.util.List;

/**
 * DTO que representa un Chat dentro del sistema.
 * Contiene los mensajes asociados y la lista de miembros participantes.
 */
public record ChatDTO(

        /** Identificador único del chat */
        Long id,

        /** Lista de IDs de los mensajes asociados al chat */
        List<Long> messageIds,

        /** Lista de IDs de los usuarios (Guest) que participan en el chat */
        List<Long> memberIds
) {}
