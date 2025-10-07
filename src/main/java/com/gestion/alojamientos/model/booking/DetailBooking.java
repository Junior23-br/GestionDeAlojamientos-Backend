package com.gestion.alojamientos.model.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.gestion.alojamientos.model.accomodation.Services;
import com.gestion.alojamientos.model.transaction.ServiceFee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detail_booking")
public class DetailBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuest;

    @Column(name = "price_per_night", nullable = false)
    private Double priceNightAccommodation;

    @Column(name = "sub_total", nullable = false)
    private Double subTotal;

    @Column(name = "discount")
    private Double discount; // Cambiado de Optional<Double> a Double

    // Relación One-to-One con ServiceFee
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "service_fee_id")
    private ServiceFee serviceFee;

    // Relación Many-to-Many con Services (tabla intermedia)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "detail_booking_services",
            joinColumns = @JoinColumn(name = "detail_booking_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Services> listServices;

    // Relación inversa One-to-One con Booking
    @OneToOne(mappedBy = "detaillBooking")
    private Booking booking;
}