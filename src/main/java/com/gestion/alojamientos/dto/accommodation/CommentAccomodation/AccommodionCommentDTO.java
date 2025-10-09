package com.gestion.alojamientos.dto.accommodation.CommentAccomodation;

import java.time.LocalDateTime;

public record AccommodionCommentDTO(

        Long id,
        String text,
        LocalDateTime creationDate,
        String authorName,
        Boolean isVisible,
        Long accomodationId,
        Long respondeHostId
) {
}
