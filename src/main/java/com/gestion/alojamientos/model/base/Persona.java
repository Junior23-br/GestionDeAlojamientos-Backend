package com.gestion.alojamientos.model.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.util.Date;
/**
 * Clase abstracta base que define los atributos comunes para Persona.
 * Sirve como superclase mapeada para entidades como Usuario y Anfitrion.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass //Indicar que es superclase pero no se mapea como clase
public abstract class Persona {
    /***
    *Identificador único generado automaticamente por la bd+
    * No se puede modificar luego de crearse
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("ID interno único de la persona creada automaticamente por el sistema")
    private Long id;
    /**
    *Nombre de la persona
    * Campo obligatorio
     */
    @Column(nullable = false,length = 50)
    @Comment("Nombre de la persona")
    private String nombre;
    /**
     *Apellido de la persona
     * Campo obligatorio
     */
    @Column(nullable = false, length = 60)
    @Comment("Apellido de la persona")
    private String apellido;
    /**
    * Telefono de la persona
    * Campo obligatorio
     */
    @Column(nullable = false, length = 10)
    @Comment("Numero telefonico de contacto")
    private String telefono;
    /**
     *Fecha de nacimiento de la persona
     * Campo obligatorio
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    @Comment("Fecha de nacimiento de la persona")
    private Date fecha_nacimiento;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;//gay el que lo encuentre
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Embedded
    private User user; //Credenciales incrustado

    //Estado de eliminado, activo, column length 20, false nullable, para eliminacion de usuario y anfitrion en service



}
