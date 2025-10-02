package com.gestion.alojamientos.model.accomodation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ubication")
public class Ubication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    // Relación Many-to-One con Cities
    @Enumerated(EnumType.STRING)
    @Column(name = "city", nullable = false, length = 100)
    private Cities city;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;
}