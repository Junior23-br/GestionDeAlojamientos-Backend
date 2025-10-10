package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Message.MessageDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.AccommodationCalificationDTO;
import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.AccommodionCommentDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
// import com.gestion.alojamientos.mapper.AdminMapper;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.model.accomodation.CommentAccomodation;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.user.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gestion.alojamientos.exception.*;

import java.util.List;

/**
 * Servicio que define las operaciones principales para la gestión de administradores.
 * Incluye métodos que permitirán registrar, editar, eliminar lógicamente
 * y obtener administradores por ID o correo electrónico.
 * Cada operación lanzará excepciones específicas en caso de errores.
 */
public interface AdminService {

    AdminDto registerAdmin(CreateAdminDto dto) throws RepeatedElementException;

    AdminDto editAdmin(Long id, EditAdminDto dto) throws ElementNotFoundException;

    void deleteAdmin(Long id) throws ElementNotFoundException;

    AdminDto getAdminById(Long id) throws ElementNotFoundException;

    AdminDto getAdminByEmail(String email) throws ElementNotFoundException;

    // 1. Usuarios (Guests)
    List<GuestDto> getAllGuests();
    GuestDto getGuestById(Long id);
    GuestDto getGuestByEmail(String email);
    GuestDto changeGuestStatus(Long id, StatesOfGuest newState);
    void deleteGuest(Long id);
    List<BookingDTO> getGuestBookingsHistory(Long guestId);

    // 2. Hosts
    List<HostDTO> getAllHosts();
    HostDTO getHostById(Long id);
    HostDTO getHostByEmail(String Email);
    HostDTO changeHostStatus(Long id, StatesOfHost newState);
    void deleteHost(Long id);


//    HostMetricsDTO getHostPerformance(Long id);
    List<BookingDTO> getHostBookingsHistory(Long id);
    List<AccommodationDTO> getHostAccommodationHistory(Long id);
    List<AccommodionCommentDTO> getHostCommentsAccommodationHistory(Long id);
    List<CommentHostDTO> getHostCommentsHistory(Long id);
    List<AccommodationCalificationDTO> getHostAccommodationCalificationHistory(Long id);
    // 3. Alojamientos
    List<AccommodationDTO> getAllAccommodations();
    AccommodationDTO getAccommodationById(Long id);
//    AccommodationDTO approveAccommodation(Long id);
//    AccommodationDTO suspendAccommodation(Long id, String reason);
    void deleteAccommodation(Long id);
//    AccommodationMetricsDTO getAccommodationMetrics(Long id);

    // 4. Reservas
    List<BookingDTO> getAllBookings();
    BookingDTO getBookingById(Long id);
//    BookingDTO resolveBookingConflict(Long bookingId, String resolution);
//    BookingGlobalStatsDTO getBookingStatistics();

//    // 5. Reportes
//    SystemReportDTO generateFinancialReport(LocalDate startDate, LocalDate endDate);
//    SystemReportDTO generateUserActivityReport(LocalDate startDate, LocalDate endDate);
//    SystemReportDTO generatePlatformPerformanceReport(LocalDate startDate, LocalDate endDate);
//    File exportReport(ReportType type, String format);

//    // 6. Configuración del Sistema
//    void updatePlatformCommission(double percentage);
//    void updateHostCommission(double percentage);
//    void updateCancellationPolicy(String policyText);
//    void updateUsagePolicy(String policyText);
//    void triggerSystemBackup();
//    List<ActivityLogDTO> getSystemLogs();

    // 7. Moderación de Contenido
//    List<ReportDTO> getAllReports();
//    ReportDTO classifyReport(Long reportId, ReportCategory category);
    void approveComment(Long commentId);
    void deleteComment(Long commentId);
    void deletePhoto(Long photoId);

    // 8. Comunicaciones
    void sendEmailToUser( MessageDTO messageDTO);
    void sendEmailToAllHosts(Long adminId);
    void sendEmailToAllGuests(Long adminId);

    // 9. Métricas Generales
//    PlatformStatsDTO getPlatformStats();
}