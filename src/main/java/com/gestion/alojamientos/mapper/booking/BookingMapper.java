package com.gestion.alojamientos.mapper.booking;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.transaction.FinancialAccount;
import com.gestion.alojamientos.model.transaction.Voucher;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import org.mapstruct.*;

/**
 * Mapper para convertir entre Booking y BookingDTO.
 */
@Mapper(
        componentModel = "spring",
        uses = { DetailBookingMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)

public interface BookingMapper {

    // ========================================================
    //        ENTITY → DTO
    // ========================================================
    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "detailBookingId", source = "detailBooking.id")
    @Mapping(target = "voucherId", source = "voucher.id")
    @Mapping(target = "accommodationId", source = "accomodation.id")
    @Mapping(target = "detailBookingDTO", source = "detailBooking")
    BookingDTO toDto(Booking booking);

    // ========================================================
    //        DTO → ENTITY
    // ========================================================
    @InheritInverseConfiguration
    @Mapping(target = "paymentMethod", expression = "java(mapFinancialAccount(dto.paymentMethodId()))")
    @Mapping(target = "guest", expression = "java(mapGuest(dto.guestId()))")
    @Mapping(target = "detailBooking", expression = "java(mapDetailBooking(dto.detailBookingDTO(), dto.detailBookingId()))")
    @Mapping(target = "voucher", expression = "java(mapVoucher(dto.voucherId()))")
    @Mapping(target = "accomodation", expression = "java(mapAccomodation(dto.accommodationId()))")
    Booking toEntity(BookingDTO dto);

    // ========================================================
    //        MÉTODOS AUXILIARES
    // ========================================================

    default FinancialAccount mapFinancialAccount(Long id) {
        if (id == null) return null;
        FinancialAccount account = new FinancialAccount();
        account.setId(id);
        return account;
    }

    default Guest mapGuest(Long id) {
        if (id == null) return null;
        Guest guest = new Guest();
        guest.setId(id);
        return guest;
    }

    default DetailBooking mapDetailBooking(DetailBookingDTO dto, Long id) {
        if (dto != null) {
            DetailBooking detail = new DetailBooking();
            detail.setId(dto.id() != null ? dto.id() : id);
            detail.setCheckInDate(dto.checkInDate());
            detail.setCheckOutDate(dto.checkOutDate());
            detail.setNumberOfGuest(dto.numberOfGuest());
            detail.setPriceNightAccommodation(dto.priceNightAccommodation());
            detail.setSubTotal(dto.subTotal());
            detail.setDiscount(dto.discount());
            return detail;
        }
        if (id != null) {
            DetailBooking detail = new DetailBooking();
            detail.setId(id);
            return detail;
        }
        return null;
    }

    default Voucher mapVoucher(Long id) {
        if (id == null) return null;
        Voucher voucher = new Voucher();
        voucher.setId(id);
        return voucher;
    }

    default Accomodation mapAccomodation(Long id) {
        if (id == null) return null;
        Accomodation accomodation = new Accomodation();
        accomodation.setId(id);
        return accomodation;
    }
}
