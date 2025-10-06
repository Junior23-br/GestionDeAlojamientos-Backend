package com.gestion.alojamientos.model.transaction;


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
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación One-to-One con Voucher
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucherList;

    // Relación Many-to-One con NormalUser
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id", nullable = false)
    private Guest holder;
}