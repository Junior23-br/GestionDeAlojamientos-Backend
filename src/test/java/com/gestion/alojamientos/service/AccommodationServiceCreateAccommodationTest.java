package com.gestion.alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion.alojamientos.dto.accommodation.AccommodationCreateDTO;
import com.gestion.alojamientos.dto.accommodation.AccommodationDTO;
import com.gestion.alojamientos.dto.accommodation.Ubication.UbicationCreateDTO;
import com.gestion.alojamientos.mapper.accomodation.AccommodationMapper;
import com.gestion.alojamientos.model.accomodation.Accomodation;
import com.gestion.alojamientos.model.accomodation.ApprovalStatus;
import com.gestion.alojamientos.model.accomodation.OperationalStatus;
import com.gestion.alojamientos.model.accomodation.Ubication;
import com.gestion.alojamientos.model.users.Host;
import com.gestion.alojamientos.repository.accomodation.AccommodationRepo;
import com.gestion.alojamientos.repository.accomodation.UbicationRepo;
import com.gestion.alojamientos.repository.user.HostRepo;
import com.gestion.alojamientos.service.Impl.AccommodationServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceCreateAccommodationTest {

    @Mock
    private AccommodationRepo accommodationRepo;

    @Mock
    private HostRepo hostRepo;

    @Mock
    private UbicationRepo ubicationRepo;

    @Mock
    private AccommodationMapper accommodationMapper;

    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    private Host testHost;
    private AccommodationCreateDTO validCreateDTO;
    private Accomodation savedAccommodation;
    private AccommodationDTO expectedDTO;

    @BeforeEach
    void setUp() {
        // Setup test host
        testHost = new Host();
        testHost.setId(1L);
        testHost.setName("Test Host");

        // Setup valid create DTO
        validCreateDTO = new AccommodationCreateDTO(
                "Casa de Playa",
                "HOUSE",
                "No fumar, no mascotas",
                new UbicationCreateDTO("Calle 123", "Ciudad", 40.7128, -74.0060),
                4,
                2,
                1,
                1L,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                Arrays.asList(1L, 2L)
        );

        // Setup saved accommodation
        savedAccommodation = new Accomodation();
        savedAccommodation.setId(1L);
        savedAccommodation.setTitle("Casa de Playa");
        savedAccommodation.setHost(testHost);
        savedAccommodation.setApprovalStatus(ApprovalStatus.PENDING);
        savedAccommodation.setOperationalStatus(OperationalStatus.ACTIVE);
        savedAccommodation.setCreatedTime(LocalDateTime.now());
        savedAccommodation.setUpdateTime(LocalDateTime.now());

        // Setup expected DTO
        expectedDTO = new AccommodationDTO(
                1L,
                "Casa de Playa",
                "HOUSE",
                "No fumar, no mascotas",
                1L,
                4,
                2,
                1,
                "PENDING",
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                null,
                null,
                null,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                null
        );
    }

    //  Éxito: Creación exitosa de alojamiento
    @Test
    void createAccommodation_Success() throws Exception {
        // Arrange
        when(hostRepo.findById(1L)).thenReturn(Optional.of(testHost));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa")).thenReturn(false);
        when(accommodationMapper.toEntity(validCreateDTO)).thenReturn(savedAccommodation);
        when(ubicationRepo.save(any(Ubication.class))).thenReturn(new Ubication());
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(expectedDTO);

        // Act
        AccommodationDTO result = accommodationService.createAccommodation(1L, validCreateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.title(), result.title());
        assertEquals(expectedDTO.approvalStatus(), result.approvalStatus());
        assertEquals(expectedDTO.operationalStatus(), result.operationalStatus());

        verify(hostRepo).findById(1L);
        verify(accommodationRepo).existsByHostIdAndTitle(1L, "Casa de Playa");
        verify(accommodationRepo).save(any(Accomodation.class));
    }
    // Fracaso: Host no encontrado
    @Test
    void createAccommodation_Failure_HostNotFound() {
        // Arrange
        when(hostRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> accommodationService.createAccommodation(1L, validCreateDTO));

        assertEquals("Host no encontrado con ID: 1", exception.getMessage());
        verify(hostRepo).findById(1L);
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    //  Datos inválidos: Título duplicado para el mismo host
    @Test
    void createAccommodation_InvalidData_DuplicateTitle() {
        // Arrange
        when(hostRepo.findById(1L)).thenReturn(Optional.of(testHost));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> accommodationService.createAccommodation(1L, validCreateDTO));

        assertEquals("Ya existe un alojamiento con este título para este anfitrión", exception.getMessage());
        verify(hostRepo).findById(1L);
        verify(accommodationRepo).existsByHostIdAndTitle(1L, "Casa de Playa");
        verify(accommodationRepo, never()).save(any(Accomodation.class));
    }
    //  Edge case: Servicios nulos
    @Test
    void createAccommodation_EdgeCase_NullServiceIds() throws Exception {
        // Arrange
        AccommodationCreateDTO dtoWithNullServices = new AccommodationCreateDTO(
                "Casa de Playa",
                "HOUSE",
                "No fumar, no mascotas",
                new UbicationCreateDTO("Calle 123", "Ciudad", 40.7128, -74.0060),
                4,
                2,
                1,
                1L,
                Arrays.asList("photo1.jpg", "photo2.jpg"),
                null // null service IDs
        );

        when(hostRepo.findById(1L)).thenReturn(Optional.of(testHost));
        when(accommodationRepo.existsByHostIdAndTitle(1L, "Casa de Playa")).thenReturn(false);
        when(accommodationMapper.toEntity(dtoWithNullServices)).thenReturn(savedAccommodation);
        when(ubicationRepo.save(any(Ubication.class))).thenReturn(new Ubication());
        when(accommodationRepo.save(any(Accomodation.class))).thenReturn(savedAccommodation);
        when(accommodationMapper.toDto(savedAccommodation)).thenReturn(expectedDTO);

        // Act
        AccommodationDTO result = accommodationService.createAccommodation(1L, dtoWithNullServices);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        verify(accommodationRepo).save(any(Accomodation.class));
    }
}

