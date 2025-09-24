package com.gestion.alojamientos.model.base;

import com.gestion.alojamientos.model.common.ResetCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.time.LocalDate;

/**
 * Clase base que define los atributos comunes para usuarios y anfitriones.
 * Sirve como superclase mapeada para entidades como Guest y Host.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class UserBasic {
    /**
     * Identificador único generado automáticamente por la base de datos.
     * No se puede modificar luego de crearse.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID interno único de la persona creada automáticamente por el sistema")
    private Long id;
    /**
     * Nombre de la persona.
     * Campo obligatorio.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    @Comment("Nombre de la persona")
    private String firstName;
    /**
     * Apellido de la persona.
     * Campo obligatorio.
     */
    @Column(name = "last_name", nullable = false, length = 60)
    @Comment("Apellido de la persona")
    private String lastName;
    /**
     * Teléfono de la persona.
     * Campo obligatorio.
     */
    @Column(name = "phone_number", nullable = false, length = 10)
    @Comment("Número telefónico de contacto")
    private String phoneNumber;
    /**
     * Fecha de nacimiento de la persona.
     * Campo obligatorio.
     */
    @Column(name = "birth-date", nullable = false, columnDefinition = "DATE")
    @Comment("Fecha de nacimiento de la persona")
    private LocalDate birthDate;
    /**
     * Dirección de correo del usuario único.
     * Campo obligatorio, con formato válido.
     */
    @Column(name = "email")
    @Comment("Dirección de correo electrónico única para autenticación.")
    private String email;
    /**
     * Contraseña del usuario cifrada.
     * Campo obligatorio, no se almacena en texto plano.
     */
    @Column(name = "password", nullable = false)
    @Comment("Contraseña segura de la persona y cifrada para autenticación")
    private String password;
    /**
     * Código de seguridad para restablecer la contraseña.
     * Este código es temporal y debe ser válido por un tiempo limitado.
     */
    @Embedded
    private ResetCode resetCode;
    /**
     * Estado de eliminación del usuario: activo o eliminado.
     */
    @Column(name = "status", nullable = false, length = 20)
    @Comment("Estado de eliminación del usuario: activo o eliminado")
    private String status;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public ResetCode getResetCode() {
        return resetCode;
    }
    public void setResetCode(ResetCode resetCode) {
        this.resetCode = resetCode;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
