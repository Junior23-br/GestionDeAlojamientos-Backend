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
@Table(name = "detail_voucher")
public class DetailVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price_night", nullable = false)
    private Double priceNight;

    @Column(name = "number_nights", nullable = false)
    private Integer numberNights;

    @Column(name = "sub_total", nullable = false)
    private Double subTotal;

    @Column(name = "Id_del_voucher", nullable = false)
    private Long idVoucher;
}
