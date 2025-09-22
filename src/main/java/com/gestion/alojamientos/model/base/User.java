package com.gestion.alojamientos.model.base;

import com.gestion.alojamientos.model.common.CodigoRestablecimiento;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import org.hibernate.annotations.Comment;
/**
*Clase embebida que contiene credenciales en el sistema
* Al ser embebida implica que su informacion será almacenada dentro de la misma
* tabla de la enitdad que la contiene
 */
@Embeddable // Esta clase será embebida en otras entidades, no se convierte en una tabla
@Data
public class User {
    /**
     *Direccion de correo del usuario unico
     * Campo obligatorio, con formato valido
     */
    @Column(name = "correo_electronico")
    @Comment("Dirección de correo electrónico única para autenticación.")
    private String correoElectronico;

    /**
     *Contraseña del usuario cifrada
     * Campo obligatorio, no se almacena en texto plano
     */
    @Column(name = "contrasenia",nullable = false)
    @Comment("Contraseña segura de la persona y cifrada para autenticacion")
    private String contrasenia;
    /**
     * Código de seguridad para restablecer la contraseña.
     * Este código es temporal y debe ser válido por un tiempo limitado.
     */

    @Embedded
    private CodigoRestablecimiento codigoRestablecimiento;

    public String correoElectronico() {
        return correoElectronico;
    }
        //Metodos de acceso
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public CodigoRestablecimiento getCodigoRestablecimiento() {
        return codigoRestablecimiento;
    }

    public void setCodigoRestablecimiento(CodigoRestablecimiento codigoRestablecimiento) {
        this.codigoRestablecimiento = codigoRestablecimiento;
    }

    //ROL DE USUARIO (Usuario, admin)
    //column y enumerated, false, string
}
