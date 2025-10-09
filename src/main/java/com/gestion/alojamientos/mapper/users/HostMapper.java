package com.gestion.alojamientos.mapper.users;

import com.gestion.alojamientos.dto.Host.DeleteHostDTO;
import com.gestion.alojamientos.dto.Host.HostCreateDTO;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Host.HostUpdateDTO;
import com.gestion.alojamientos.model.message.CommentHost;
import com.gestion.alojamientos.model.users.Host;
import org.mapstruct.*;

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
    @Mapping(target = "name", source = "name") // NormalUser.name → firstName
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", expression = "java(host.getBirthDate())")
    @Mapping(target = "urlProfilePhoto", source = "urlProfilePhoto")
    @Mapping(target = "listAccommodationsIds", expression = "java(mapAccommodationsToIds(host.getListAccommodations()))")
    @Mapping(target = "hostCommentIds", expression = "java(mapCommentsToIds(host.getHostCommentList()))")
    @Mapping(target = "financialAccountId", expression = "java(host.getReceiptPayment() != null ? host.getReceiptPayment().getId() : null)")
    @Mapping(target = "serviceFeeId", expression = "java(host.getServiceFee() != null ? host.getServiceFee().getId() : null)")
    HostDTO toDTO(Host host);

    // ===== DTO → ENTITY =====
    @Mapping(target = "password", ignore = true) // por seguridad
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    Host toEntity(HostDTO hostDTO);


    // ===== UPDATE DTO → ENTITY =====
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "financialAccount", ignore = true)
    void updateHostFromDTO(HostUpdateDTO dto, @MappingTarget Host host);


    // ===== DELETE DTO → ENTITY (para validación o autenticación) =====
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
    @Mapping(target = "financialAccount", ignore = true)
    Host toEntityDelete(DeleteHostDTO dto);

    // ===== CREATE DTO → ENTITY =====
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "email") // opcional, depende de tu lógica
    @Mapping(target = "urlProfilePhoto", ignore = true)
    @Mapping(target = "listAccommodations", ignore = true)
    @Mapping(target = "hostCommentList", ignore = true)
    @Mapping(target = "receiptPayment", ignore = true)
    @Mapping(target = "serviceFee", ignore = true)
    @Mapping(target = "financialAccount", ignore = true)
    Host toEntity(HostCreateDTO dto);
    // ===== Métodos auxiliares para IDs =====
    default List<Long> mapAccommodationsToIds(List<com.gestion.alojamientos.model.accomodation.Accomodation> list) {
        if (list == null) return null;
        return list.stream()
                .map(com.gestion.alojamientos.model.accomodation.Accomodation::getId)
                .collect(Collectors.toList());
    }

    default List<Long> mapCommentsToIds(List<CommentHost> list) {
        if (list == null) return null;
        return list.stream()
                .map(CommentHost::getId)
                .collect(Collectors.toList());
    }

    // ===== Conversión opcional =====
    default java.time.LocalDate convertDateToLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }
}
