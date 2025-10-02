package com.gestion.alojamientos.model;

import com.gestion.alojamientos.model.base.NormalUser;
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
@Table(name = "financial_account")
public class FinancialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName; // Bank name

    @Column(name = "number_account", nullable = false, length = 50)
    private String numberAccount; // Account number

    @ManyToOne
    @JoinColumn(name = "holder_id")
    private Guest carHolder; // Account holder

    @Column(name = "available_balance", nullable = false)
    private Double avalaibleBalance; // Available balance
}
