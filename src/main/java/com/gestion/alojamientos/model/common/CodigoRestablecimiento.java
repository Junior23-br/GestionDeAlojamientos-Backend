package com.gestion.alojamientos.model.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa los codigos de seguridad para la activacion y restablecimiento de contraseña
 * Esta marcada como embebida y eso implica que este dentro de otras entidades
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CodigoRestablecimiento {
    /**
     * Codigo usado para restablecer contraseña
     */
    @Column(name = "codigo_restablecimiento")
    private String codigo_restablecimiento;
    /**
     * Fecha y hora de expiración del código de restablecimiento de contraseña.
     */
    @Column(name = "codigo_restablecimiento_expiracion")
    private LocalDateTime codigo_restablecimiento_expiracion;
}
