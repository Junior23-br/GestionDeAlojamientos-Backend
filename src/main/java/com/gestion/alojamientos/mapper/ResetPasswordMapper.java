package com.gestion.alojamientos.mapper;

import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import org.mapstruct.*;

/**
 * Mapper de MapStruct para crear un ResetPasswordDto a partir de una entidad de usuario.
 * Solo mapea el campo email del objeto fuente, mientras que resetCode y newPassword
 * se proporcionan como argumentos externos.
 */
@Mapper(componentModel = "spring")
public interface ResetPasswordMapper {

    /**
     * Convierte una entidad de usuario a ResetPasswordDto.
     * Solo toma el email de la entidad y los otros campos se pasan como argumentos.
     *
     * @param source     La entidad o DTO del usuario (debe tener getEmail()).
     * @param resetCode  Código de recuperación proporcionado externamente.
     * @param newPassword Nueva contraseña proporcionada externamente.
     * @return ResetPasswordDto con los tres valores.
     */
    @Mapping(target = "email", source = "source.email")
    @Mapping(target = "resetCode", expression = "java(resetCode)")
    @Mapping(target = "newPassword", expression = "java(newPassword)")
    ResetPasswordDto toResetPasswordDto(Object source, String resetCode, String newPassword);
}
