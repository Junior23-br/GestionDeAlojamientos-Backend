package com.gestion.alojamientos.service.Impl;
import com.gestion.alojamientos.dto.Host.HostDTO;
import com.gestion.alojamientos.dto.Message.MessageDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.CalificationAccommodation.AccommodationCalificationDTO;
import com.gestion.alojamientos.dto.accommodation.CommentAccomodation.AccommodionCommentDTO;
import com.gestion.alojamientos.dto.accommodation.CommentHost.CommentHostDTO;
import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
import com.gestion.alojamientos.dto.booking.BookingDTO;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.ElementNotFoundException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.mapper.UserLoginMapper;
import com.gestion.alojamientos.mapper.accomodation.AccommodationCalificationMapper;
import com.gestion.alojamientos.mapper.users.AdminMapper;
import com.gestion.alojamientos.mapper.users.GuestMapper;
import com.gestion.alojamientos.mapper.users.HostMapper;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.mapper.booking.BookingMapper;
import com.gestion.alojamientos.mapper.accomodation.CommentAccomodationMapper;
import com.gestion.alojamientos.mapper.accomodation.CommentHostMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.AccomodationCalification;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.base.SuperUser;
import com.gestion.alojamientos.model.enums.StatesAdmin;
import com.gestion.alojamientos.model.enums.StatesOfGuest;
import com.gestion.alojamientos.model.enums.StatesOfHost;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.repository.user.AdminRepository;
import com.gestion.alojamientos.repository.user.GuestRepository;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.booking.BookingRepo;
import com.gestion.alojamientos.repository.accomodation.CommentAccomodationRepo;
import com.gestion.alojamientos.repository.accomodation.CommentHostRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de administración (Admin).
 *
 * Nota:
 * - Algunos métodos delegan a otros servicios/repositorios (notificaciones, fotos) que
 *   podrían no estar implementados aún en tu proyecto. En esos casos está marcado con // TODO.
 */
@Service
@Transactional
public class AdminServiceImpl implements com.gestion.alojamientos.service.AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private AccommodationCalificationMapper calificationMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailServiceImpl emailServiceImpl;
    @Autowired
    private ResetCodeServiceImpl resetCodeServiceImpl;

    // Repositorios y mappers necesarios para operaciones de moderación/consulta
    @Autowired(required = false)
    private GuestRepository guestRepository;
    @Autowired(required = false)
    private GuestMapper guestMapper;

    @Autowired(required = false)
    private HostRepo hostRepo;
    @Autowired(required = false)
    private HostMapper hostMapper;

    @Autowired(required = false)
    private AccommodationRepo accommodationRepo;
    @Autowired(required = false)
    private AccommodationMapper accommodationMapper;

    @Autowired(required = false)
    private BookingRepo bookingRepo;
    @Autowired(required = false)
    private BookingMapper bookingMapper;

    @Autowired(required = false)
    private CommentAccomodationRepo commentAccomodationRepo;
    @Autowired(required = false)
    private CommentAccomodationMapper commentAccommodationMapper;

    @Autowired(required = false)
    private CommentHostRepo commentHostRepo;
    @Autowired(required = false)
    private CommentHostMapper commentHostMapper;
    @Autowired
    private AccommodationCalificationMapper accommodationCalificationMapper;


    @Override
    public AdminDto registerAdmin(CreateAdminDto dto) throws RepeatedElementException {
        if (adminRepository.existsByEmail(dto.email())) {
            throw new RepeatedElementException("Ya existe un administrador con el correo proporcionado.");
        }

        Admin admin = new Admin();
        admin.setEmail(dto.email());
        // Encriptamos la contraseña antes de guardar
        admin.setPassword(passwordEncoder.encode(dto.password()));
        // username: si no lo envía el DTO, podemos derivarlo del email (antes de @) o setear igual al email.
        String username = dto.email().split("@")[0];
        admin.setUsername(username);
        // nivel de acceso por defecto (podría variar según la política)
        admin.setAccess_level(1);
        admin.setStatesAdmin(StatesAdmin.ACTIVE);
        System.out.println(admin.getEmail());

        Admin saved = adminRepository.save(admin);
        return adminMapper.toDTO(saved);
    }

    @Override
    public AdminDto editAdmin(Long id, EditAdminDto dto) throws ElementNotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id));
        // No se permite cambiar email ni id según DTO
        // Cambiar contraseña: la interfaz EditAdminDto define password; la actualizamos encriptada
        admin.setPassword(passwordEncoder.encode(dto.password()));
        // Persistir cambios
        Admin updated = adminRepository.save(admin);
        return adminMapper.toDTO(updated);
    }

    @Override
    public void deleteAdmin(Long id) throws ElementNotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id));
        adminRepository.delete(admin);
    }

    @Override
    public AdminDto getAdminById(Long id) throws ElementNotFoundException {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id));
        return adminMapper.toDTO(admin);
    }

    @Override
    public AdminDto getAdminByEmail(String email) throws ElementNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con email: " + email));
        return adminMapper.toDTO(admin);
    }

    // ===== 1. Usuarios (Guests) =====

    @Override
    public List<GuestDto> getAllGuests() {
        if (guestRepository == null || guestMapper == null) {
            throw new UnsupportedOperationException("GuestRepository o GuestMapper no están disponibles (TODOS LOS GUESTS).");
        }
        return guestRepository.findAll()
                .stream()
                .map(guestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GuestDto getGuestById(Long id) {
        if (guestRepository == null || guestMapper == null) {
            throw new UnsupportedOperationException("GuestRepository o GuestMapper no están disponibles (GET GUEST).");
        }
        return guestRepository.findById(id)
                .map(guestMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con ID: " + id));
    }

    @Override
    public GuestDto getGuestByEmail(String email) {
        if (guestRepository == null || guestMapper == null) {
            throw new UnsupportedOperationException("GuestRepository o GuestMapper no están disponibles (GET GUEST BY EMAIL).");
        }
        return guestRepository.findByEmail(email)
                .map(guestMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con email: " + email));
    }

    @Override
    public GuestDto changeGuestStatus(Long id, StatesOfGuest newState) {
        if (guestRepository == null || guestMapper == null) {
            throw new UnsupportedOperationException("GuestRepository o GuestMapper no están disponibles (CHANGE GUEST STATUS).");
        }
        var guest = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con ID: " + id));
        guest.setState(newState);
        var saved = guestRepository.save(guest);
        return guestMapper.toDto(saved);
    }

    @Override
    public void deleteGuest(Long id) {
        if (guestRepository == null) {
            throw new UnsupportedOperationException("GuestRepository no está disponible (DELETE GUEST).");
        }
        // Podríamos hacer soft delete, pero aquí se delega a cambiar estado si se desea:
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con ID: " + id));
        guest.setState(StatesOfGuest.DELETED);
        guestRepository.save(guest);
    }

    @Override
    public List<BookingDTO> getGuestBookingsHistory(Long guestId) {
        if (bookingRepo == null || bookingMapper == null) {
            throw new UnsupportedOperationException("BookingRepo o BookingMapper no están disponibles (GUEST BOOKINGS).");
        }
        if (!guestRepository.existsById(guestId)) {
            throw new EntityNotFoundException("Huésped no encontrado con ID: " + guestId);
        }
        return bookingRepo.findByGuestIdWithDetails(guestId)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    // ===== 2. Hosts =====

    @Override
    public List<HostDTO> getAllHosts() {
        if (hostRepo == null || hostMapper == null) {
            throw new UnsupportedOperationException("HostRepo o HostMapper no están disponibles (GET ALL HOSTS).");
        }
        return hostRepo.findAll()
                .stream()
                .map(hostMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HostDTO getHostById(Long id) {
        if (hostRepo == null || hostMapper == null) {
            throw new UnsupportedOperationException("HostRepo o HostMapper no están disponibles (GET HOST BY ID).");
        }
        return hostRepo.findById(id)
                .map(hostMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con ID: " + id));
    }

    @Override
    public HostDTO getHostByEmail(String Email) {
        if (hostRepo == null || hostMapper == null) {
            throw new UnsupportedOperationException("HostRepo o HostMapper no están disponibles (GET HOST BY EMAIL).");
        }
        return hostRepo.findByEmail(Email)
                .map(hostMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con email: " + Email));
    }

    @Override
    public HostDTO changeHostStatus(Long id, StatesOfHost newState) {
        if (hostRepo == null || hostMapper == null) {
            throw new UnsupportedOperationException("HostRepo o HostMapper no están disponibles (CHANGE HOST STATUS).");
        }
        var host = hostRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con ID: " + id));
        host.setStatus(newState);
        var saved = hostRepo.save(host);
        return hostMapper.toDTO(saved);
    }

    @Override
    public void deleteHost(Long id) {
        if (hostRepo == null) {
            throw new UnsupportedOperationException("HostRepo no está disponible (DELETE HOST).");
        }
        var host = hostRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Host no encontrado con ID: " + id));
        host.setStatus(StatesOfHost.DELETED);
        hostRepo.save(host);
    }

    @Override
    public List<BookingDTO> getHostBookingsHistory(Long id) {
        if (bookingRepo == null || bookingMapper == null) {
            throw new UnsupportedOperationException("BookingRepo o BookingMapper no están disponibles (HOST BOOKINGS).");
        }
        if (!hostRepo.existsById(id)) {
            throw new EntityNotFoundException("Host no encontrado con ID: " + id);
        }
        return bookingRepo.findByGuestIdWithDetails(id)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationDTO> getHostAccommodationHistory(Long id) {
        if (accommodationRepo == null || accommodationMapper == null) {
            throw new UnsupportedOperationException("AccommodationRepo o AccommodationMapper no están disponibles (HOST ACCOMMODATIONS).");
        }
        if (!hostRepo.existsById(id)) {
            throw new EntityNotFoundException("Host no encontrado con ID: " + id);
        }
        return accommodationRepo.findByHostId(id)
                .stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodionCommentDTO> getHostCommentsAccommodationHistory(Long id) {
        if (commentAccomodationRepo == null || commentAccommodationMapper == null) {
            throw new UnsupportedOperationException("CommentAccomodationRepo o CommentAccommodationMapper no están disponibles.");
        }

        return commentAccomodationRepo.findByAuthorId(id)
                .stream()
                .map(commentAccommodationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentHostDTO> getHostCommentsHistory(Long id) {
        if (commentHostRepo == null || commentHostMapper == null) {
            throw new UnsupportedOperationException("CommentHostRepo o CommentHostMapper no están disponibles.");
        }
        return commentHostRepo.findByReceiverId(id)
                .stream()
                .map(commentHostMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AccommodationCalificationDTO> getHostAccommodationCalificationHistory(Long id) {
        if (accommodationMapper == null) {
            throw new UnsupportedOperationException("AccommodationMapper no está disponible (CALIFICATIONS).");
        }
        List<AccommodationCalificationDTO> list = new ArrayList<>();
        for(Accomodation accommodation : accommodationRepo.findByHostId(id)) {
            for(AccomodationCalification accomodationCalification: accommodation.getAccomodationCalificationList()){
                list.add(accommodationCalificationMapper.toDTO(accomodationCalification));
            }
        }
        return list;
    }
    // ===== 3. Alojamientos =====

    @Override
    public List<AccommodationDTO> getAllAccommodations() {
        if (accommodationRepo == null || accommodationMapper == null) {
            throw new UnsupportedOperationException("AccommodationRepo o AccommodationMapper no están disponibles (GET ALL ACCOMMODATIONS).");
        }
        return accommodationRepo.findAll()
                .stream()
                .map(accommodationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccommodationDTO getAccommodationById(Long id) {
        if (accommodationRepo == null || accommodationMapper == null) {
            throw new UnsupportedOperationException("AccommodationRepo o AccommodationMapper no están disponibles (GET ACCOMMODATION BY ID).");
        }

        return accommodationRepo.findById(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + id));
    }

    @Override
    public void deleteAccommodation(Long id) {
        if (accommodationRepo == null) {
            throw new UnsupportedOperationException("AccommodationRepo no está disponible (DELETE ACCOMMODATION).");
        }
        Accomodation accommodation = accommodationRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alojamiento no encontrado con ID: " + id));

        accommodation.setOperationalStatus(OperationalStatus.DELETED);
        accommodationRepo.save(accommodation);
    }

    // ===== 4. Reservas =====

    @Override
    public List<BookingDTO> getAllBookings() {
        if (bookingRepo == null || bookingMapper == null) {
            throw new UnsupportedOperationException("BookingRepo o BookingMapper no están disponibles (GET ALL BOOKINGS).");
        }
        return bookingRepo.findAll()
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDTO getBookingById(Long id) {
        if (bookingRepo == null || bookingMapper == null) {
            throw new UnsupportedOperationException("BookingRepo o BookingMapper no están disponibles (GET BOOKING BY ID).");
        }
        return bookingRepo.findByIdWithAllDetails(id)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));
    }

    // ===== Moderación de contenido =====

    // ===== Comunicaciones =====

    @Override
    public void sendEmailToUser(MessageDTO messageDTO) {
        String email = (guestRepository.findById(messageDTO.receiverId())).get().getEmail();
        emailServiceImpl.sendEmaiil(email, messageDTO);
    }
    //Codigo aleatorio, busca al guest,
    // genera el código, lo asigna al guest y lo envía por email.
    @Override
    public String generateResetCode(String email) throws ElementNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + email));
        return resetCodeServiceImpl.generateAndSendCode(admin);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        Admin admin = adminRepository.findById(userId)
                .orElseThrow(() -> new ElementNotFoundException("Usuario no encontrado con ID: " + userId));
        if(!passwordEncoder.matches(dto.currentPassword(), admin.getPassword())) {
            throw  new InvalidElementException("Contraseña incorrecta");
        }
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if(!dto.newPassword().matches(passwordPattern)) {
            throw new InvalidElementException("La nueva contraseña no cumple las politicas de privacidad");
        }
        admin.setPassword(passwordEncoder.encode(dto.newPassword()));
        adminRepository.save(admin);
    }

    @Override
    public UserLoginDTO login(UserLoginDTO dto) throws InvalidElementException {
        Admin admin = adminRepository.findByEmail(dto.email())
                .orElseThrow(() -> new InvalidElementException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.password(), admin.getPassword())) {
            throw new InvalidElementException("Credenciales inválidas");
        }
        AdminDto adminDto = adminMapper.toDTO(admin);
        System.out.println(adminDto.email());
        return dto;
    }

    //Verificar que el codigo exista, actualiza la contraseña, verifica expiracion codigo
    @Override
    public void resetPassword(ResetPasswordDto dto) throws InvalidElementException, ElementNotFoundException {
        Admin admin = adminRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ElementNotFoundException("Huésped no encontrado con email: " + dto.email()));
        //validar codigo
        try {
            // Validar código
            resetCodeServiceImpl.validateCode(admin, dto.resetCode());
        } catch (InvalidElementException e) {
            if (e.getMessage().equals("Código de recuperación expirado.")) {
                // Generar y enviar un nuevo código automáticamente
                String newCode = resetCodeServiceImpl.generateAndSendCode(admin);
                throw new InvalidElementException("El código ha expirado. Se ha enviado un nuevo código a " + dto.email() + ". Por favor, usa el nuevo código para restablecer tu contraseña.");
            }
            // Si es otro error (ej. código incorrecto o no generado), relanzar la excepción original
            throw e;
        }
        //validar politica calve
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.newPassword().matches(passwordPattern)) {
            throw new InvalidElementException("La nueva contraseña no cumple con la política de seguridad.");
        }
        admin.setPassword(passwordEncoder.encode(dto.newPassword()));
        // Limpiar el resetCode para que no pueda reutilizarse
        admin.setResetCode(null); //limpiar
        adminRepository.save(admin);
    }
}