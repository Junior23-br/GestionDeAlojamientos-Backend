package com.gestion.alojamientos.dto.usuario;

import java.util.Date;

/**
*DTO representa los datos completos de un usuario en el sistema
* Incluye la info basica heredada y su informacion especifica
*
 */
public record UsuarioDto (
        //Base de la clase abstracta
        Long id,
        String nombre,
        String apellido,
        String telefono,
        Date fecha_nacimiento,
        String correo_electronico

){

}
