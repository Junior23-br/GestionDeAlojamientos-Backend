package com.gestion.alojamientos.model.transaction;

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
@Table(name = "service_fee")
public class ServiceFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "value", nullable = false)
    private double value;

    // Enum para el tipo de tarifa
    @Enumerated(EnumType.STRING)
    @Column(name = "type_fee", nullable = false, length = 50)
    private TypeFee typeFee;

    @Column(name = "prom_calification_minimum")
    private double promCalificationMinimun;

    @Column(name = "number_bookings_minimum")
    private Integer numberBookingsMinimum;
}
