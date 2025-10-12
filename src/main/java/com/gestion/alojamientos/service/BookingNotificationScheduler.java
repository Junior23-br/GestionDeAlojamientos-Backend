package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.mapper.booking.DetailBookingMapper;
import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio programado para enviar notificaciones automáticas de check-in.
 * Ejecuta tareas programadas para enviar recordatorios 24 horas antes del check-in.
 */
@Service
public class BookingNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingNotificationScheduler.class);

    private final BookingRepo bookingRepo;
    private final EmailService emailService;
    private final BookingMapper bookingMapper;
    private final DetailBookingMapper detailBookingMapper;

    public BookingNotificationScheduler(BookingRepo bookingRepo, EmailService emailService, 
                                      BookingMapper bookingMapper, DetailBookingMapper detailBookingMapper) {
        this.bookingRepo = bookingRepo;
        this.emailService = emailService;
        this.bookingMapper = bookingMapper;
        this.detailBookingMapper = detailBookingMapper;
    }

    /**
     * Tarea programada que se ejecuta cada hora para enviar notificaciones de check-in.
     * Busca reservas cuyo check-in ocurra dentro de las próximas 24 horas y envía correos
     * tanto al huésped como al host.
     */
    @Scheduled(fixedRate = 3600000) // Ejecuta cada hora (3600000 ms = 1 hora)
    @Transactional(readOnly = true)
    public void sendCheckInReminders() {
        log.info("Iniciando proceso de envío de recordatorios de check-in - {}", LocalDateTime.now());
        
        try {
            // Calcular fechas para las próximas 24 horas
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
            
            log.info("Buscando reservas con check-in entre {} y {}", tomorrow, dayAfterTomorrow);
            
            // Buscar reservas confirmadas con check-in en las próximas 24 horas
            List<Booking> upcomingBookings = findUpcomingCheckIns(tomorrow, dayAfterTomorrow);
            
            if (upcomingBookings.isEmpty()) {
                log.info("No se encontraron reservas con check-in en las próximas 24 horas");
                return;
            }
            
            log.info("Encontradas {} reservas con check-in próximo", upcomingBookings.size());
            
            // Procesar cada reserva
            int successCount = 0;
            int errorCount = 0;
            
            for (Booking booking : upcomingBookings) {
                try {
                    processCheckInReminder(booking);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    log.error("Error procesando recordatorio para reserva ID {}: {}", 
                            booking.getId(), e.getMessage(), e);
                }
            }
            
            log.info("Proceso completado - Exitosos: {}, Errores: {}", successCount, errorCount);
            
        } catch (Exception e) {
            log.error("Error crítico en el proceso de recordatorios de check-in", e);
        }
    }

    /**
     * Busca reservas confirmadas con check-in en el rango de fechas especificado.
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de reservas encontradas
     */
    private List<Booking> findUpcomingCheckIns(LocalDate startDate, LocalDate endDate) {
        return bookingRepo.findBookingsByCheckInDateRange(startDate, endDate)
                .stream()
                .filter(booking -> booking.getBookingState() == StatesOfBooking.CONFIRMED)
                .toList();
    }

    /**
     * Procesa el envío de recordatorio de check-in para una reserva específica.
     * Envía correos tanto al huésped como al host.
     * 
     * @param booking Reserva a procesar
     * @throws InvalidElementException si hay error en el envío de correos
     */
    private void processCheckInReminder(Booking booking) throws InvalidElementException {
        log.info("Procesando recordatorio para reserva ID: {} - Check-in: {}", 
                booking.getId(), booking.getDetailBooking().getCheckInDate());
        
        // Convertir entidades a DTOs
        BookingDTO bookingDTO = bookingMapper.toDto(booking);
        DetailBookingDTO detailBookingDTO = detailBookingMapper.toDto(booking.getDetailBooking());
        
        // Obtener emails
        String guestEmail = booking.getGuest().getEmail();
        String hostEmail = booking.getAccomodation().getHost().getEmail();
        
        log.info("Enviando recordatorio a huésped: {} y host: {}", guestEmail, hostEmail);
        
        // Enviar correo al huésped
        try {
            emailService.sendCheckInReminderEmail(guestEmail, bookingDTO, detailBookingDTO);
            log.info("Recordatorio enviado exitosamente al huésped: {}", guestEmail);
        } catch (Exception e) {
            log.error("Error enviando recordatorio al huésped {}: {}", guestEmail, e.getMessage());
            throw e;
        }
        
        // Enviar correo al host
        try {
            emailService.sendCheckInReminderEmail(hostEmail, bookingDTO, detailBookingDTO);
            log.info("Recordatorio enviado exitosamente al host: {}", hostEmail);
        } catch (Exception e) {
            log.error("Error enviando recordatorio al host {}: {}", hostEmail, e.getMessage());
            throw e;
        }
    }

    /**
     * Método manual para enviar recordatorios de check-in para una fecha específica.
     * Útil para testing o envío manual de notificaciones.
     * 
     * @param targetDate Fecha objetivo para buscar reservas
     * @return Número de recordatorios enviados
     */
    @Transactional(readOnly = true)
    public int sendCheckInRemindersForDate(LocalDate targetDate) {
        log.info("Enviando recordatorios manuales para fecha: {}", targetDate);
        
        LocalDate nextDay = targetDate.plusDays(1);
        List<Booking> bookings = findUpcomingCheckIns(targetDate, nextDay);
        
        int sentCount = 0;
        for (Booking booking : bookings) {
            try {
                processCheckInReminder(booking);
                sentCount++;
            } catch (Exception e) {
                log.error("Error enviando recordatorio manual para reserva ID {}: {}", 
                        booking.getId(), e.getMessage());
            }
        }
        
        log.info("Recordatorios manuales enviados: {}", sentCount);
        return sentCount;
    }

    /**
     * Verifica el estado del scheduler y muestra estadísticas.
     * 
     * @return Información del estado actual
     */
    public String getSchedulerStatus() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);
        
        List<Booking> upcomingBookings = findUpcomingCheckIns(tomorrow, dayAfterTomorrow);
        
        return String.format("Scheduler activo - Próximos check-ins: %d (entre %s y %s)", 
                upcomingBookings.size(), tomorrow, dayAfterTomorrow);
    }
}
