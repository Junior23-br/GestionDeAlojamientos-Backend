package com.gestion.alojamientos.mapper.accomodation;

import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.AccommodionCommentDTO;
import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.AccomodationCommentUpdateDTO;
import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.CreateAccommodatiionCommentDTO;
import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.DelateAccomodationCommentDTO;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.CommentAccomodation;
import com.gestion.alojamientos.model.users.Guest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper que convierte entre la entidad CommentAccomodation y sus diferentes DTOs.
 *
 * - Evita exponer entidades completas, usando IDs o valores planos.
 * - Controla relaciones bidireccionales (autor, alojamiento, respuesta del host).
 * - Se integra con Spring mediante componentModel = "spring".
 */
@Mapper(componentModel = "spring")
public interface CommentAccomodationMapper {

    // ===== ENTITY → DTO =====
    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "text")
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "authorName", expression = "java(comment.getAuthor() != null ? comment.getAuthor().getName() : null)")
    @Mapping(target = "isVisible", source = "isVisible")
    @Mapping(target = "accomodationId", expression = "java(comment.getAccomodation() != null ? comment.getAccomodation().getId() : null)")
    @Mapping(target = "respondeHostId", expression = "java(comment.getRespondeHost() != null ? comment.getRespondeHost().getId() : null)")
    AccommodionCommentDTO toDTO(CommentAccomodation comment);

    // ===== ENTITY LIST → DTO LIST =====
    default List<AccommodionCommentDTO> toDTOList(List<CommentAccomodation> comments) {
        if (comments == null) return null;
        return comments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ===== CREATE DTO → ENTITY =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "author", expression = "java(mapGuestFromId(dto.authorId()))")
    @Mapping(target = "accomodation", expression = "java(mapAccomodationFromId(dto.accomodationId()))")
    @Mapping(target = "respondeHost", ignore = true)
    @Mapping(target = "isVisible", constant = "true")
    CommentAccomodation toEntity(CreateAccommodatiionCommentDTO dto);

    // ===== UPDATE DTO → ENTITY =====
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "accomodation", ignore = true)
    @Mapping(target = "respondeHost", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    void updateFromDto(AccomodationCommentUpdateDTO dto, @MappingTarget CommentAccomodation comment);

    // ===== DELETE DTO → ENTITY (solo para validación) =====
    @Mapping(target = "text", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "isVisible", ignore = true)
    @Mapping(target = "respondeHost", ignore = true)
    @Mapping(target = "accomodation", ignore = true)
    @Mapping(target = "author", expression = "java(mapGuestFromId(dto.authorId()))")
    CommentAccomodation toEntity(DelateAccomodationCommentDTO dto);

    // ===== Métodos auxiliares =====
    /**
     * Crea una instancia parcial de Guest solo con su ID.
     * Se usa para evitar cargar toda la entidad desde la base de datos.
     */
    default Guest mapGuestFromId(Long id) {
        if (id == null) return null;
        Guest guest = new Guest();
        guest.setId(id);
        return guest;
    }

    /**
     * Crea una instancia parcial de Accomodation solo con su ID.
     */
    default Accomodation mapAccomodationFromId(Long id) {
        if (id == null) return null;
        Accomodation accomodation = new Accomodation();
        accomodation.setId(id);
        return accomodation;
    }
}