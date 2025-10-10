package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
/**
 * Firma del metodo envio de correos
 */
public interface EmailService {
    /**
     * Enviar email como codigo de recuperacion en pdf
     * @param email del destinatario
     * @param code codigo de recuperacion generado
     * @throws InvalidElementException si el email es invalido
     */
    void SendResetCodeEmail (String email,String code) throws InvalidElementException;/**
     *  Envía un email con los detalles de una nueva reserva como pdf
     * @param email del destinatario
     * @param bookingDTO detalles de la reservq
     * @throws InvalidElementException si el email es invalido
     */
    void SendNewBookingEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException;

    /**
     *Envia un email con los detalles de una reserva cancelada, tipo pdf
     * @param email email del destinatario
     * @param bookingDTO detalles de la reserva cancelada
     * @throws InvalidElementException si el email es invalido o falla el envio
     */
    void sendCancelledBookingEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException;
    /**
     *
     * @param email email del destinatario huesped
     * @param bookingDTO detalles de la reserva proxima a check-in (24 horas antes del check in)
     * @throws InvalidElementException si el email es invalido o falla el envio
     */
    void sendCheckInReminderEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException;

    /**
     * Enviar email de bienvenida al crear cuenta nueva
     * @param email del destinatario
     * @param guestDto nombre del huesped
     * @throws InvalidElementException si falla el envio
     */
    void sendWelcomeEmail(String email,GuestDto guestDto) throws InvalidElementException;

    /**
     * Enviar email de agradecimiento luego de check out y aca añadir lo de reseña
     * @param email email del huesped
     * @param bookingDTO detalles de la reserva ya finalizada
     * @throws InvalidElementException si falla el envio
     */
    void sendCheckOutThanksEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException;
    /**
     *ORGANIZAR BIEN SI ESTE METODO VA O NOOO CON ANTIONIO Y SANTI
     *  Envía un email de confirmación para cambio de rol de guest a host
     * @param Email Email del usuario
     * @param guestDto Nombre del usuario
     * @throws InvalidElementException Si falla el envío
     */
    void sendRoleChangeConfirmationEmail(String Email, GuestDto guestDto) throws InvalidElementException;
    /**
     * Envía un email de confirmación de eliminación de cuenta.
     * @param Email Email del huésped.
     * @param guestDto Nombre del huésped.
     * @throws InvalidElementException Si falla el envío.
     */
    void sendAccountDeletionConfirmationEmail(String Email, GuestDto guestDto) throws InvalidElementException;

}
