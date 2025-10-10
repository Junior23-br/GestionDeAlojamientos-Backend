package com.gestion.alojamientos.mapper.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.dto.transaction.ServiceFee.ServiceFeeDTO;
import com.gestion.alojamientos.mapper.transaction.ServiceFeeMapper;
import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.transaction.ServiceFee;
import com.gestion.alojamientos.model.transaction.TypeFee;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        uses = {ServiceFeeMapper.class}
)
public interface DetailBookingMapper {

    // ========================================================
    //            ENTITY → DTO
    // ========================================================
    @Mapping(target = "serviceFeeDTO", source = "serviceFee")
    @Mapping(target = "idBooking", source = "booking.id")
    DetailBookingDTO toDto(DetailBooking entity);

    // ========================================================
    //            DTO → ENTITY
    // ========================================================
    @InheritInverseConfiguration
    @Mapping(target = "booking", ignore = true) // Se asigna desde BookingMapper o el servicio
    @Mapping(target = "serviceFee", source = "serviceFeeDTO")
    DetailBooking toEntity(DetailBookingDTO dto);

    // ========================================================
    //            MÉTODOS AUXILIARES
    // ========================================================

    /** Convierte entidad ServiceFee → DTO */
    default ServiceFeeDTO mapServiceFeeToDTO(ServiceFee serviceFee) {
        if (serviceFee == null) return null;
        return new ServiceFeeDTO(
                serviceFee.getId(),
                serviceFee.getDescription(),
                serviceFee.getValue(),
                serviceFee.getTypeFee().toString(),
                serviceFee.getPromCalification(),
                serviceFee.getNumberBookingsMinimum()
        );
    }

    /** Convierte DTO → entidad ServiceFee */
    default ServiceFee mapDTOToServiceFee(ServiceFeeDTO dto) {
        if (dto == null) return null;
        ServiceFee entity = new ServiceFee();
        entity.setId(dto.id());
        entity.setDescription(dto.description());
        entity.setValue(dto.value());
        entity.setTypeFee(TypeFee.valueOf(dto.typeFee()));
        entity.setPromCalification(dto.promCalificationMinimun());
        entity.setNumberBookingsMinimum(dto.numberBookingsMinimum());
        return entity;
    }

    /** Mapea lista de servicios a sus IDs (si luego lo necesitas en otros DTOs) */
    default List<Long> mapServicesToIds(List<Services> services) {
        if (services == null) return null;
        return services.stream()
                .map(Services::getId)
                .collect(Collectors.toList());
    }

    /** Mapea lista de IDs a entidades Services */
    default List<Services> mapIdsToServices(List<Long> ids) {
        if (ids == null) return null;
        return ids.stream()
                .map(id -> {
                    Services s = new Services();
                    s.setId(id);
                    return s;
                })
                .collect(Collectors.toList());
    }
}