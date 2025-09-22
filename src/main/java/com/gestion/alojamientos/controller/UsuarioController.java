package com.gestion.alojamientos.controller;

import com.gestion.alojamientos.dto.usuario.CrearUsuarioDto;
import com.gestion.alojamientos.dto.usuario.EditarUsuarioDto;
import com.gestion.alojamientos.dto.usuario.EliminarUsuarioDto;
import com.gestion.alojamientos.dto.usuario.UsuarioDto;
import com.gestion.alojamientos.model.Usuario;
import com.gestion.alojamientos.service.UsuarioService;
import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Será el controlador REST el cual gestionara las operaciones relacionadas
 * con usuarios. Además proporciona endpoints para poder registrar, eliminar editar y consultar usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    /**
     * Registra un usuario en el sistema
     * @param dto Objeto con los datos del usuario a crear
     * @return ResponseEntity con el DTO del usuario creado
     */
    @PostMapping
    public ResponseEntity<UsuarioDto> registrarUsuario(@RequestBody CrearUsuarioDto dto) {
        try {
            return ResponseEntity.ok(usuarioService.registrarUsuario(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    /**
     * Editar la info del usuario existente
     * @param dto Objeto con los datos a actualizar.
     * @return ResponseEntity con el DTO del usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> editarUsuario(@PathVariable Long id, @RequestBody EditarUsuarioDto dto) {
        try {
            return ResponseEntity.ok(usuarioService.editarUsuario(id, dto));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        }

    /**
     *  Eliminar un usuario del sistema
     * @param id Identificador único del usuario
     * @param dto objeto con los datos a eliminar
     * @return ResponseEntity vacio si la operación es exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id, @RequestBody EliminarUsuarioDto dto) {
       try {
           usuarioService.eliminarUsuario(id, dto);
           return ResponseEntity.noContent().build();
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(null);
       }
    }

    /**
     * Obtener los detalles de un usuario
     * @param id Identificador único del usuario.
     * @return ResponseEntity con el DTO del usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     *  valor de prueba
     * @return resultado
     */

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("¡Funciona la prueba de salida de enodpint!");
    }
}
