package com.gestion.alojamientos.model.accomodation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accomodation_calification")
public class AccomodationCalification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; // Identificador único

    @Column(name = "cleanliness", nullable = false)
    private Integer cleanliness; // Limpieza

    @Column(name = "comfort", nullable = false)
    private Integer comfort; // Comodidad

    @Column(name = "location", nullable = false)
    private Integer location; // Ubicación

    @Column(name = "accuracy_of_listing", nullable = false)
    private Integer accuracyOfListing; // Precisión del anuncio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accomodation_id", nullable = false)
    private Accomodation accomodation;

    @Column(name = "value_for_money", nullable = false)
    private Integer communicationHost; // Comunicación con el anfitrión

    @Column(name = "prom", nullable = false)
    private Double prom; // Promedio de calificación
}
