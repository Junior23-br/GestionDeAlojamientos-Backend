package com.gestion.alojamientos.mapper.accomodation;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.model.message.CommentHost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentHostMapper {


    // Convierte de entidad a DTO
    @Mapping(source = "host.id", target = "hostId")
    @Mapping(source = "guest.id", target = "guestId")
    CommentHostDTO toDto(CommentHost entity);

    // Convierte de DTO a entidad
    @Mapping(source = "hostId", target = "host.id")
    @Mapping(source = "guestId", target = "guest.id")
    CommentHost toEntity(CommentHostDTO dto);
}