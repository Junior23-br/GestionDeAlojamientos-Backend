package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.admin.AdminDto;
import com.gestion.alojamientos.dto.admin.CreateAdminDto;
import com.gestion.alojamientos.dto.admin.EditAdminDto;
// import com.gestion.alojamientos.mapper.AdminMapper;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gestion.alojamientos.exception.*;

/**
 * Servicio que define las operaciones principales para la gestión de administradores.
 * Incluye métodos que permitirán registrar, editar, eliminar lógicamente
 * y obtener administradores por ID o correo electrónico.
 * Cada operación lanzará excepciones específicas en caso de errores.
 */
public interface AdminService {

    AdminDto registerAdmin(CreateAdminDto dto) throws RepeatedElementException;

    AdminDto editAdmin(Long id, EditAdminDto dto) throws ElementNotFoundException;

    void deleteAdmin(Long id) throws ElementNotFoundException;

    AdminDto getAdminById(Long id) throws ElementNotFoundException;

    AdminDto getAdminByEmail(String email) throws ElementNotFoundException;
}