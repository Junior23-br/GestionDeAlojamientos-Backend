package com.gestion.alojamientos.service;

import com.gestion.alojamientos.dto.usuario.CrearUsuarioDto;
import com.gestion.alojamientos.dto.usuario.EditarUsuarioDto;
import com.gestion.alojamientos.dto.usuario.EliminarUsuarioDto;
import com.gestion.alojamientos.dto.usuario.UsuarioDto;
import com.gestion.alojamientos.exception.ElementoNoEncontradoException;
import com.gestion.alojamientos.exception.ElementoNoValidoException;
import com.gestion.alojamientos.exception.ElementoRepetidoException;
import com.gestion.alojamientos.mapper.UsuarioMapper;
import com.gestion.alojamientos.model.Usuario;
import com.gestion.alojamientos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class UsuarioService {
    /**
     * Servicio que define las operaciones principales para la gestion
     * Incluye metodos que permitirán registrar, editar, eliminar
     * Obtener usuarios por nombre o correo electronico
     * Cada operacion lanzará excepciones especificas
     */
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioMapper usuarioMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * @param dto DTO con los datos del usuario a registrar
     * @return DTO del usuario creadp
     * @throws ElementoRepetidoException Si el correo o numero telefonico ya estan eegistrados
     * @throws ElementoNoValidoException Si son datos que no cumplen con las validaciones
     */
    public UsuarioDto registrarUsuario(CrearUsuarioDto dto) throws ElementoRepetidoException, ElementoNoValidoException {
        if (usuarioRepository.existsByUser_CorreoElectronico(dto.correo_electronico())) {
            throw new ElementoRepetidoException("El correo electronico ya esta registrado");
        }
        if (usuarioRepository.existsByTelefono(dto.telefono())) {
            throw new ElementoRepetidoException("El teléfono ya está registrado");
        }
        if (dto.fecha_nacimiento().isAfter((LocalDate.now()))) {
            throw new ElementoNoValidoException("Fecha nacimiento es invalida");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.getUser().setContrasenia(passwordEncoder.encode(dto.contrasenia())); // Encripta la contraseña
        usuarioRepository.save(usuario);
        return usuarioMapper.toDto(usuario);
    }
        /**
         *@param id  Identificador único del usuario.
         *@param dto DTO con los datos a actualizar.
         *@return DTO del usuario actualizado.
         *@throws ElementoNoEncontradoException Si el usuario no existe.
         */
        public UsuarioDto editarUsuario(Long id, EditarUsuarioDto dto) throws ElementoNoEncontradoException {
            Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNoEncontradoException("Usuario no encontrado con ID: " + id));
            usuarioMapper.updateFromDto(dto, usuario);
            return usuarioMapper.toDto(usuarioRepository.save(usuario));
        }
        /**
         * Elimina lógicamente un usuario del sistema.
         *
         * @param id  Identificador único del usuario.
         * @param dto DTO con la contraseña para validar la eliminación.
         * @throws ElementoNoEncontradoException Si el usuario no existe.
         * @throws ElementoNoValidoException Si la contraseña no coincide.
         */
        public void eliminarUsuario(Long id, EliminarUsuarioDto dto ) throws ElementoNoEncontradoException, ElementoNoValidoException {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ElementoNoEncontradoException("Usuario no encontrado con ID: " + id));
        if(!passwordEncoder.matches(dto.contrasenia(), usuario.getUser().getContrasenia())) {
            throw new ElementoNoValidoException("Contrasenia incorrecta");
        }
        usuarioRepository.delete(usuario);
        }

    /**
     *
     * @param id identificacor del usuario
     * @return dto del usuario obtenido
     * @throws ElementoNoEncontradoException si el usuario no xiste
     */
        public UsuarioDto obtenerUsuarioPorId (Long id) throws ElementoNoEncontradoException {
            return usuarioMapper.toDto(usuarioRepository.findById(id).orElseThrow(() -> new ElementoNoEncontradoException("Usuario no encontrado con ID: " + id)));
        }

}

