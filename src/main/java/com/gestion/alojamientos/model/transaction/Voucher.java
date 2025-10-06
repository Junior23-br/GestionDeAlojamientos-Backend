package com.gestion.alojamientos.model.transaction;

import java.time.LocalDateTime;
import java.util.Optional;

import com.gestion.alojamientos.model.booking.Booking;
import com.gestion.alojamientos.model.users.Guest;

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
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    // Relación Many-to-One con Guest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "sub_total", nullable = false)
    private Double subTotal;

    @Column(name = "tax", nullable = false)
    private Double tax;

    @Column(name = "discount")
    private Double discount; // Cambiado de Optional<Double> a Double

    @Column(name = "total", nullable = false)
    private Double total;

    // Enum para el estado del voucher
    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_state", nullable = false, length = 50)
    private VoucherState voucherState;

    // Relación Many-to-One con FinancialAccount
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private FinancialAccount paymentMethod;

    // Relación One-to-One con DetailVoucher
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "detail_voucher_id", nullable = false)
    private DetailVoucher detailVoucher;

    // Relación One-to-One con Booking
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}