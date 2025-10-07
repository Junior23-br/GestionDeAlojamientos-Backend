package com.gestion.alojamientos.mapper.booking;

import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.mapper.transaction.ServiceFeeMapper;
import com.gestion.alojamientos.model.booking.DetailBooking;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {ServiceFeeMapper.class}
)
public interface DetailBookingMapper {

    // ==========================================
    // ENTIDAD → DTO
    // ==========================================
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "checkInDate", target = "checkInDate"),
            @Mapping(source = "checkOutDate", target = "checkOutDate"),
            @Mapping(source = "numberOfGuest", target = "numberOfGuest"),
            @Mapping(source = "priceNightAccommodation", target = "priceNightAccommodation"),
            @Mapping(source = "subTotal", target = "subTotal"),
            @Mapping(source = "discount", target = "discount"),
            @Mapping(source = "serviceFee", target = "serviceFeeDTO"),
            @Mapping(source = "listServices", target = "listServices"),
            @Mapping(source = "booking.id", target = "idBooking")
    })
    DetailBookingDTO toDTO(DetailBooking entity);

    // ==========================================
    // DTO → ENTIDAD
    // ==========================================
    @InheritInverseConfiguration
    @Mapping(target = "booking", ignore = true)
    DetailBooking toEntity(DetailBookingDTO dto);
}

