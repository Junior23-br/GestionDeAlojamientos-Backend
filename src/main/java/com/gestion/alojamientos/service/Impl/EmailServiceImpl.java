package com.gestion.alojamientos.service.Impl;

import com.gestion.alojamientos.dto.Message.MessageDTO;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.booking.detailBooking.DetailBookingDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.service.EmailService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Inmplementación del servicio para enviar correos con pdf adjunto
 */
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    //Validar formato de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void SendResetCodeEmail(String email, String code) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] emailBytes = generateResetCodePdf(email, code);
            sendEmail(email, "El código de recuperación de contraseña",
                    "Adjunto a este archivo encontrará el código de recuperación, el cual " +
                            "solo es válido por 15 mínutos", "ResetCode.pdf", emailBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de recuperación" + e.getMessage());
        }


    }

    @Override
    public void SendNewBookingEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] pdfBytes = generateNewBookinPdf(email, bookingDTO, detailBookingDTO);
            sendEmail(email, "Confirmación de Nueva Reserva",
                    "Adjunto encontrarás los detalles de tu nueva reserva.",
                    "NewBooking.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de nueva reserva: " + e.getMessage());
        }


    }

    @Override
    public void sendEmaiil(String email, MessageDTO messageDTO) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] pdfBytes = generateNormalEmail(email, messageDTO);
            sendEmail(email, "Mensaje de administrador",
                    "Adjunto encontrarás informacion",
                    "Information.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de nueva reserva: " + e.getMessage());
        }


    }


    private byte[] generateNormalEmail(String email, MessageDTO messageDTO) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // Formato de fecha legible
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            // Encabezado
            document.add(new Paragraph("📧 MENSAJE NUEVO")
                    .setBold()
                    .setFontSize(16));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Detalles del mensaje:").setBold());
            // Información del mensaje
            document.add(new Paragraph("Fecha de envío: " + sdf.format(messageDTO.createDate())));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Contenido del mensaje:").setBold());
            document.add(new Paragraph(messageDTO.text()));
            // Si tiene adjunto
            if (messageDTO.doc() != null && messageDTO.doc().length > 0) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Este mensaje contiene un archivo adjunto.").setItalic());
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Enviado a: " + email));
            document.add(new Paragraph("Gracias por utilizar nuestro sistema de mensajería."));
            document.close();
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void sendCancelledBookingEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException {
        emailValidator(email);
         try {
            byte[] pdfBytes = generateCancelledBookingPdf(email, bookingDTO,detailBookingDTO);
            sendEmail(email, "Confirmación de Cancelación de Reserva",
                    "Adjunto a este documento encontrarás los detalles de tu reserva cancelada.",
                    "CancelledBooking.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de cancelación: " + e.getMessage());
        }


    }

    @Override
    public void sendCheckInReminderEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] pdfBytes = generateCheckInReminderPdf(email, bookingDTO, detailBookingDTO);
            sendEmail(email, "Recordatorio de Check-In",
                    "Tu check-in está programado para mañana. Adjunto los detalles de tu reserva.",
                    "CheckInReminder.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de recordatorio: " + e.getMessage());
        }



    }

    @Override
    public void sendWelcomeEmail(String email, GuestDto guestDto) throws InvalidElementException {
        emailValidator(email);
         try {
            byte[] pdfBytes = generateCreationPdf(email, guestDto);
            sendEmail(email, "Bienvenido a Rincón del Viajero",
                    "Adjunto encontrarás tu confirmación de registro. ¡Gracias por unirte!",
                    "AccountCreation.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de creación de cuenta: " + e.getMessage());
        }


    }

    @Override
    public void sendCheckOutThanksEmail(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws InvalidElementException {
        emailValidator(email);
          try {
            byte [] pdfBytes = generateCheckOutPdf(email, bookingDTO, detailBookingDTO);
            sendEmail(email, "Gracias por tu estancia",
                    "Adjunto a este correo encontraras un archivo con un resumen de tu reserva." +
                            "¡Esperamos verte pronto de nuevo!", "CheckOutThanks.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("error al enviar el email de agradecimiento post check-out: " +e.getMessage());
        }

    }

    @Override
    public void sendRoleChangeConfirmationEmail(String email, GuestDto guestDto) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] pdfBytes = generateRoleChangePdf(email, guestDto);
            sendEmail(email, "Confirmación de Cambio a Anfitrión",
                    "Adjunto encontrarás la confirmación de tu nuevo rol como anfitrión.",
                    "RoleChange.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de cambio de rol: " + e.getMessage());
        }
    }

    @Override
    public void sendAccountDeletionConfirmationEmail(String email, GuestDto guestDto) throws InvalidElementException {
        emailValidator(email);
        try {
            byte[] pdfBytes = generateAccountDeletionPdf(email, guestDto);
            sendEmail(email, "Confirmación de Eliminación de Cuenta",
                    "Adjunto encontrarás la confirmación de que tu cuenta ha sido eliminada.",
                    "AccountDeletion.pdf", pdfBytes);
        } catch (Exception e) {
            throw new InvalidElementException("Error al enviar el email de eliminación de cuenta: " + e.getMessage());
        }
    }


    /**
     * validar el formato del email y que no este nulo
     *
     * @param email email a verificar
     * @throws InvalidElementException si el email es invalido
     */
    private void emailValidator(String email) throws InvalidElementException {
        if (email == null || email.isEmpty()) {
            throw new InvalidElementException("El email no puede estar vacio");
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new InvalidElementException("Email invalido: " + email);
        }
    }

    /**
     * Enviar el email con el archivo adjunto
     *
     * @param email          del destinatario
     * @param subject        asunto del correo
     * @param body           cuerpo del correo
     * @param attachmentName nombre del archivo
     * @param pdf            tmñ pdf
     * @throws Exception si falla el envio
     */
    private void sendEmail(String email, String subject, String body, String attachmentName, byte[] pdf)
            throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body);
        ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(pdf, "application/pdf");
        mimeMessageHelper.addAttachment(attachmentName, byteArrayDataSource);
        mailSender.send(mimeMessage);
    }
//    /**
//     * Construye el nombre completo desde GuestDTO.
//     * @param guestDTO DTO del huésped
//     * @return Nombre completo o "Huésped" si está vacío
//     */
//    private String buildGuestName(GuestDto guestDTO) {
//        String firstName = guestDTO.firstName() != null ? guestDTO.firstName() : "";
//        String lastName = guestDTO.lastName() != null ? guestDTO.lastName() : "";
//        String fullName = (firstName + " " + lastName).trim();
//        return fullName.isEmpty() ? "Huésped" : fullName;
//    }

    /**
     * Genera un pdf para el codigo de recuperacion
     *
     * @param email del huesped
     * @param code  codigo de recuperacion
     * @return byte del pdf
     * @throws Exception si falla la generacion
     */
    private byte[] generateResetCodePdf(String email, String code) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Recuperación de Contraseña - Rincon del Viajero."));
        document.add(new Paragraph("Email: " + email));
        document.add(new Paragraph("Código de recuperación: " + code));
        document.add(new Paragraph("Válido hasta: " + LocalDateTime.now().plusMinutes(15)));
        document.add(new Paragraph("Por favor, usa este código para restablecer tu contraseña, vigente solo en los próximos 15 minutos."));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Genera un pdf para una reserva nueva
     *
     * @param bookingDTO detalles de la reserva
     * @return tmñ pdf, bytes
     * @throws Exception si falla la generación del pdf
     */
    private byte[] generateNewBookinPdf(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Confirmacíon de la nueva reserva: "));
       document.add(new Paragraph("ID de la reserva: " +detailBookingDTO.id()));
       document.add(new Paragraph("Alojamiento ID: "+bookingDTO.id()));
       document.add(new Paragraph("Fecha de Check-In: " + detailBookingDTO.checkInDate()));
       document.add(new Paragraph("Fecha de Check-Out: " +
        (detailBookingDTO.checkOutDate() != null ?
         detailBookingDTO.checkOutDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
         "N/A")));
       document.add(new Paragraph("Estado: " + bookingDTO.bookingState().toString()));
       document.add(new Paragraph("Precio Total: $" + bookingDTO.totalPrice()));
       document.add(new Paragraph("Gracias por tu reserva. Te esperamos en la fecha indicada."));
       document.close();
       return byteArrayOutputStream.toByteArray();
    }


    /**
     * Generar pdf para una reserva cancelada
     * @param bookingDTO detalles de la reserva
     * @return bytes del pdf
     * @throws Exception si falla la generación
     */
     private byte [] generateCancelledBookingPdf(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws
            Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Confirmación de Cancelación de Reserva -El  Rincon del Viajero"));
        document.add(new Paragraph("ID de Reserva: " + detailBookingDTO.id()));
        document.add(new Paragraph("Alojamiento ID: " + bookingDTO.accommodationId()));
        document.add(new Paragraph("Fecha de Check-In: " + detailBookingDTO.checkInDate()));
        document.add(new Paragraph("Fecha de Check-Out: " +
        (detailBookingDTO.checkOutDate() != null ?
         detailBookingDTO.checkOutDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
         "N/A")));
        document.add(new Paragraph("Estado: " + bookingDTO.bookingState().toString()));
        document.add(new Paragraph("Precio Total: $" + bookingDTO.totalPrice()));
        document.add(new Paragraph("Tu reserva ha sido cancelada. Si necesitas ayuda no dudes en contáctanos."));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Genera un PDF para el recordatorio de check-in
     * @param bookingDTO Detalles de la reserva
     * @return Bytes del PDF
     * @throws Exception Si falla la generación
     */
    private byte [] generateCheckInReminderPdf(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Recordatorio de Check-In - Alojamiento Quindío"));
        document.add(new Paragraph("ID de Reserva: " + bookingDTO.id()));
        document.add(new Paragraph("Alojamiento ID: " + bookingDTO.accommodationId()));
        document.add(new Paragraph("Fecha de Check-In: " + detailBookingDTO.checkInDate()));
        document.add(new Paragraph("Estado: " + bookingDTO.bookingState().toString()));
        document.add(new Paragraph("Precio Total: $" + bookingDTO.totalPrice()));
        document.add(new Paragraph("Tu check-in es mañana. ¡Prepárate para tu estadía!"));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Genera un pdf para dar la bienvenida al usuario
     * @param email del destinatario
     * @param guestDto del usuario a crear cuenta
     * @return bytes del pdf
     * @throws Exception si falla el envioo
     */
     private byte [] generateCreationPdf(String email, GuestDto guestDto) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Bienvenido a Rincón del Viajero "));
        document.add(new Paragraph("Nombre: " + guestDto.name()));
        document.add(new Paragraph("Email: " + guestDto.email()));
        document.add(new Paragraph("Tu cuenta ha sido creada exitosamente. ¡Empieza a explorar alojamientos!"));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private byte [] generateCheckOutPdf(String email, BookingDTO bookingDTO, DetailBookingDTO detailBookingDTO) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Agradecimiento Post Check-Out - El Rincón Del Viajero"));
        document.add(new Paragraph("ID de la reserva: " +detailBookingDTO.id()));
        document.add(new Paragraph("Alojamiento ID: " + bookingDTO.accommodationId()));
        document.add(new Paragraph("Fecha de Check-Out: " +
        (detailBookingDTO.checkOutDate() != null ?
         detailBookingDTO.checkOutDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
         "N/A")));
        document.add(new Paragraph("Precio Total: $" + bookingDTO.totalPrice()));
        document.add(new Paragraph("Gracias por tu escogernos."));
        document.close();
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Genera un pdf para la confirmacion de cambio de rol
     * @param email del destinatario
     * @param guestDto del huesped
     * @return bytes del pdf
     * @throws Exception si falla la generacion
     */
    private byte[] generateRoleChangePdf(String email, GuestDto guestDto) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Confirmación de Cambio de Rol - Alojamiento Quindío"));
        document.add(new Paragraph("Nombre: " + guestDto.name()));
        document.add(new Paragraph("Email: " + guestDto.email()));
        document.add(new Paragraph("Tu cuenta ha sido actualizada a rol de Anfitrión. Ahora puedes gestionar alojamientos."));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Genera un PDF para la confirmación de eliminación de cuenta
     * @param email del huesped
     * @param guestDto Dtoo del huesped
     * @return bytes del pdf
     * @throws Exception si falla la generación del pdf
     */
    private byte[] generateAccountDeletionPdf(String email, GuestDto guestDto) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Confirmación de Eliminación de Cuenta - Alojamiento Quindío"));
        document.add(new Paragraph("Nombre: " + guestDto.name()));
        document.add(new Paragraph("Email: " + email));
        document.add(new Paragraph("Tu cuenta ha sido eliminada lógicamente. Si fue un error, contáctanos."));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }




}
