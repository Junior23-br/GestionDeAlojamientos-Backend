package com.gestion.alojamientos.mapper.accomodation;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.model.message.CommentHost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentHostMapper {
     // De entidad a DTO
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "sender.name", target = "senderName")
    CommentHostDTO toDto(CommentHost entity);

    // De DTO a entidad
    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    CommentHost toEntity(CommentHostDTO dto);
}