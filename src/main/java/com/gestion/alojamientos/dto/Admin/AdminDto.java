package com.gestion.alojamientos.dto.Admin;
/**
 * Un DTO (Data Transfer Object) es un objeto plano que se usa para intercambiar datos entre capas
 * Tiene la información basica del Admin, no la contraseña por temas de seguridad
 *
 */
public record AdminDto(Long id, String email) {
}
