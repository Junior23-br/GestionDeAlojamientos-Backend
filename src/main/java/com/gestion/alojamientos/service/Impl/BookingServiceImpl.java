package com.gestion.alojamientos.service.impl;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestion.alojamientos.dto.booking.*;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingCreateDTO;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.model.accomodation.*;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.booking.DetailBooking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.booking.*;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.service.BookingService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private AccommodationRepo accommodationRepo;
    @Autowired
    private GuestRepository guestRepo;
    @Autowired
    private DetailBookingRepo detailBookingRepo;
    @Autowired
    private BookingMapper bookingMapper;

    @Override
    public BookingDTO createBooking(BookingCreateDTO createBookingDTO) throws Exception {
        // Validar que el alojamiento existe y está operativo
        Accomodation accommodation = accommodationRepo.findById(createBookingDTO.idAccommodation())
            .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado"));

        if (accommodation.getOperationalStatus() != OperationalStatus.ACTIVE ||
            accommodation.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new Exception("El alojamiento no está disponible para reservas");
        }

        // OBTENER GUEST DEL CONTEXTO DE SEGURIDAD - ACTUALMENTE NO DISPONIBLE
        // Guest guest = getAuthenticatedGuest();
        // Por ahora usaremos un guest por defecto para testing
        Guest guest = guestRepo.findById(1L) // TODO: Reemplazar con guest autenticado
            .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado"));

        // Validar fechas
        validateBookingDates(createBookingDTO.detailBookingCreateDTO());

        // Validar capacidad de huéspedes
        validateGuestCapacity(createBookingDTO.detailBookingCreateDTO(), accommodation);

        // Validar disponibilidad (no solapamiento)
        if (bookingRepo.isAccommodationBooked(
            createBookingDTO.idAccommodation(),
            createBookingDTO.detailBookingCreateDTO().checkInDate(),
            createBookingDTO.detailBookingCreateDTO().checkOutDate()
        )) {
            throw new Exception("El alojamiento no está disponible en las fechas seleccionadas");
        }

        // PUNTO PARA PAYMENT SERVICE
        // paymentService.validatePayment(createBookingDTO.idPaymentMethod(), createBookingDTO.totalPrice());

        // Crear DetailBooking primero
        DetailBooking detailBooking = buildDetailBooking(createBookingDTO.detailBookingCreateDTO());
        DetailBooking savedDetailBooking = detailBookingRepo.save(detailBooking);

        // Crear entidad Booking
        Booking booking = buildBookingEntity(createBookingDTO, accommodation, guest, savedDetailBooking);
        Booking savedBooking = bookingRepo.save(booking);

        // Actualizar relación bidireccional
        savedDetailBooking.setBooking(savedBooking);
        detailBookingRepo.save(savedDetailBooking);

        // PUNTO PARA NOTIFICATION SERVICE
        // notificationService.sendBookingConfirmation(savedBooking);

        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public boolean cancelBooking(DeleteBookingDTO cancelBookingDTO) throws Exception {
        Booking booking = bookingRepo.findByIdWithAllDetails(cancelBookingDTO.idBooking())
            .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        // Validar propiedad (usuario o anfitrión)
        validateCancellationPermission(booking, cancelBookingDTO);

        // Validar política de 48 horas
        validateCancellationPolicy(booking);

        // Actualizar estado
        booking.setBookingState(StatesOfBooking.CANCELLED);
        booking.setUpdateTime(LocalDateTime.now());
        
        Booking cancelledBooking = bookingRepo.save(booking);

        // PUNTO PARA NOTIFICATION SERVICE
        // notificationService.sendCancellationConfirmation(cancelledBooking);

        // PUNTO PARA PAYMENT SERVICE (reembolso)
        // paymentService.processRefund(cancelledBooking);

        return true;
    }

    @Override
    public List<BookingDTO> getGuestBookings(Long guestId) throws Exception {
        // Validar que el huésped existe
        if (!guestRepo.existsById(guestId)) {
            throw new EntityNotFoundException("Huésped no encontrado");
        }

        List<Booking> bookings = bookingRepo.findByGuestIdWithDetails(guestId);
        return bookings.stream()
            .map(bookingMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> getAccommodationBookings(Long accommodationId) throws Exception {
        // Validar que el alojamiento existe
        if (!accommodationRepo.existsById(accommodationId)) {
            throw new EntityNotFoundException("Alojamiento no encontrado");
        }

        List<Booking> bookings = bookingRepo.findByAccommodationId(accommodationId);
        return bookings.stream()
            .map(bookingMapper::toDto)
            .collect(Collectors.toList());
    }

    // ===== MÉTODOS PRIVADOS DE VALIDACIÓN =====

    private void validateBookingDates(DetailBookingCreateDTO detailDTO)  throws Exception {
        LocalDate checkIn = detailDTO.checkInDate();
        LocalDate checkOut = detailDTO.checkOutDate();
        LocalDate today = LocalDate.now();

        // No fechas pasadas
        if (checkIn.isBefore(today)) {
            throw new Exception("No se pueden reservar fechas pasadas");
        }

        // Mínimo 1 noche
        if (!checkOut.isAfter(checkIn)) {
            throw new Exception("La estadía mínima es de 1 noche");
        }

        // Máximo de estadía (30 días)
        if (ChronoUnit.DAYS.between(checkIn, checkOut) > 30) {
            throw new Exception("La estadía máxima permitida es de 30 días");
        }
    }

    private void validateGuestCapacity(DetailBookingCreateDTO detailDTO, Accomodation accommodation) throws Exception {
        Integer requestedGuests = detailDTO.numberOfGuest();
        Integer maxCapacity = accommodation.getMaxGuestCapacity();

        if (requestedGuests > maxCapacity) {
            throw new Exception(
                String.format("La capacidad máxima es de %d huéspedes", maxCapacity)
            );
        }

        if (requestedGuests <= 0) {
            throw new Exception("El número de huéspedes debe ser mayor a 0");
        }
    }

    private void validateCancellationPermission(Booking booking, DeleteBookingDTO cancelDTO) throws Exception {
        boolean isGuestOwner = booking.getGuest().getId().equals(cancelDTO.idGuest());
        boolean isHostOwner = booking.getAccomodation().getHost().getId().equals(cancelDTO.idHost());

        if (!isGuestOwner && !isHostOwner) {
            throw new Exception("No tienes permisos para cancelar esta reserva");
        }
    }

    private void validateCancellationPolicy(Booking booking) throws Exception {
        LocalDate checkInDate = booking.getDetailBooking().getCheckInDate();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancellationDeadline = checkInDate.atStartOfDay().minusHours(48);

        if (now.isAfter(cancellationDeadline)) {
            throw new Exception(
                "Solo se puede cancelar hasta 48 horas antes del check-in"
            );
        }
    }

    private Booking buildBookingEntity(BookingCreateDTO dto, Accomodation accommodation, Guest guest, DetailBooking detailBooking) {
        // Convertir String a Enum para bookingState
        StatesOfBooking bookingState;
        Booking booking = new Booking();
        try {
            bookingState = parseBookingState(dto.bookingState());
            booking.setBookingState(bookingState); // si no esta aqui no se inicializa XD
        } catch (Exception e) {

            e.printStackTrace();
        }
        booking.setTotalPrice(dto.totalPrice());
        booking.setPaymentStatus(dto.paymenStatus());
        booking.setCreationDate(LocalDateTime.now());
        booking.setUpdateTime(LocalDateTime.now());
        
        // Establecer relaciones
        booking.setAccomodation(accommodation);
        booking.setGuest(guest);
        booking.setDetailBooking(detailBooking);

        // ESTABLECER PAYMENT METHOD - necesitaríamos financialAccountRepo
        // FinancialAccount paymentMethod = financialAccountRepo.findById(dto.idPaymentMethod())
        //     .orElseThrow(() -> new EntityNotFoundException("Método de pago no encontrado"));
        // booking.setPaymentMethod(paymentMethod);

        return booking;
    }

    private DetailBooking buildDetailBooking(DetailBookingCreateDTO detailDTO) {
        return DetailBooking.builder()
            .checkInDate(detailDTO.checkInDate())
            .checkOutDate(detailDTO.checkOutDate())
            .numberOfGuest(detailDTO.numberOfGuest())
            .priceNightAccommodation(detailDTO.priceNightAccommodation())
            .subTotal(calculateSubTotal(detailDTO))
            .discount(detailDTO.discount())
            .listServices(new ArrayList<>()) // PENDIENTE: Asignar servicios si existen
            .build();
    }

    private Double calculateSubTotal(DetailBookingCreateDTO detailDTO) {
        long numberOfNights = ChronoUnit.DAYS.between(detailDTO.checkInDate(), detailDTO.checkOutDate());
        double subTotal = numberOfNights * detailDTO.priceNightAccommodation();
        
        if (detailDTO.discount() != null) {
            subTotal -= detailDTO.discount();
        }
        
        return Math.max(subTotal, 0.0);
    }

    private StatesOfBooking parseBookingState(String bookingStateStr) throws Exception{
        try {
            return StatesOfBooking.valueOf(bookingStateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new Exception("Estado de reserva no válido: " + bookingStateStr);
        }
    }

    // MÉTODO PARA OBTENER GUEST AUTENTICADO (CUANDO SE IMPLEMENTE SEGURIDAD)
    /*
    private Guest getAuthenticatedGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return guestRepo.findByEmail(username)
            .orElseThrow(() -> new BusinessException("Usuario no autenticado"));
    }
    */
}
