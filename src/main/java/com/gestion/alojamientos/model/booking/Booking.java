package com.gestion.alojamientos.model.booking;

import java.time.LocalDateTime;

import com.gestion.alojamientos.model.enums.StatesOfBooking;
import com.gestion.alojamientos.model.transaction.FinancialAccount;
import com.gestion.alojamientos.model.transaction.Voucher;
import com.gestion.alojamientos.model.users.Guest;

import jakarta.persistence.*;

import com.gestion.alojamientos.model.accomodation.Accomodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_state", nullable = false)
    private StatesOfBooking bookingState;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "payment_status", nullable = false)
    private Boolean paymentStatus;

    // Relación Many-to-One con FinancialAccount
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private FinancialAccount paymentMethod;

    // Relación Many-to-One con Guest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    // Relación One-to-One con DetailBooking
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "detail_booking_id", nullable = false)
    private DetailBooking detaillBooking;

    // Relación One-to-One con Voucher (opcional)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    // Relación Many-to-One con Accommodation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accomodation accomodation;
}