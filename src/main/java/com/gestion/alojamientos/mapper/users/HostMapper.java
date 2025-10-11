package com.gestion.alojamientos.mapper.users;

import com.gestion.alojamientos.dto.Host.DeleteHostDTO;
import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Host.HostUpdateDTO;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.users.Host;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper que convierte entre la entidad Host y su DTO (HostDTO).
 *
 * - Evita exponer entidades relacionadas, solo usa IDs.
 * - Ignora campos sensibles como la contraseña.
 * - Previene ciclos de mapeo con relaciones bidireccionales.
 */
@Mapper(componentModel = "spring")
public interface HostMapper {

   // ===== ENTITY → DTO =====
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "urlProfilePhoto", source = "urlProfilePhoto")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "personalDescription", source = "personalDescription")
    @Mapping(target = "role", source = "role") // NUEVO: mapeo del rol
    @Mapping(target = "listAccommodationsIds",
             expression = "java(mapAccommodationsToIds(host.getListAccommodations()))")
    @Mapping(target = "hostCommentIds",
             expression = "java(mapCommentsToIds(host.getHostCommentList()))")
    @Mapping(target = "financialAccountId",
             expression = "java(host.getReceiptPayment() != null ? host.getReceiptPayment().getId() : null)")
    @Mapping(target = "serviceFeeId",
             expression = "java(host.getServiceFee() != null ? host.getServiceFee().getId() : null)")
    HostDTO toDTO(Host host);

    // ===== DTO → ENTITY =====
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "resetCode", ignore = true)
    Host toEntity(HostDTO hostDTO);

    // ===== UPDATE DTO → ENTITY =====
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "resetCode", ignore = true)    @Mapping(target = "role", ignore = true) // usualmente no se actualiza desde el DTO
    void updateHostFromDTO(HostUpdateDTO dto, @MappingTarget Host host);

    // ===== DELETE DTO → ENTITY =====
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "urlProfilePhoto", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "resetCode", ignore = true)
    Host toEntityDelete(DeleteHostDTO dto);

    // ===== CREATE DTO → ENTITY =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "email")
    @Mapping(target = "urlProfilePhoto", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "resetCode", ignore = true)
    @Mapping(target = "personalDescription", source = "personalDescription")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", constant = "HOST") // asignación por defecto para nuevos hosts
    Host toEntity(HostCreateDTO dto);

    // ===== Métodos auxiliares =====
    default List<Long> mapAccommodationsToIds(List<com.gestion.alojamientos.model.accomodation.Accomodation> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(com.gestion.alojamientos.model.accomodation.Accomodation::getId)
                .collect(Collectors.toList());
    }

    default List<Long> mapCommentsToIds(List<CommentHost> list) {
        if (list == null) return List.of();
        return list.stream()
                .map(CommentHost::getId)
                .collect(Collectors.toList());
    }
}
