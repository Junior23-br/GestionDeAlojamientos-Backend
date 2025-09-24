package com.gestion.alojamientos.controller;
import com.gestion.alojamientos.dto.guest.CreateGuestDto;
import com.gestion.alojamientos.dto.guest.EditGuestDto;
import com.gestion.alojamientos.dto.guest.DeleteGuestDto;
import com.gestion.alojamientos.dto.guest.GuestDto;
import com.gestion.alojamientos.service.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Será el controlador REST el cual gestionara las operaciones relacionadas
 * con huespedes. Además proporciona endpoints para poder registrar, eliminar editar y consultar huespedes.
 */
@RestController
@RequestMapping("/api/guests")
public class GuestController {
    @Autowired
    private GuestService guestService;
    /**
     * Registra un huesped en el sistema
     * @param dto Objeto con los datos del huesped a crear
     * @return ResponseEntity con el DTO del huesped creado
     */
    @PostMapping
    public ResponseEntity<GuestDto> registerGuest(@RequestBody CreateGuestDto dto) {
        try {
            return ResponseEntity.ok(guestService.registerGuest(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    /**
     * Editar la info del huesped existente
     * @param dto Objeto con los datos a actualizar.
     * @return ResponseEntity con el DTO del huesped actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<GuestDto> editGuest(@PathVariable Long id, @RequestBody EditGuestDto dto) {
        try {
            return ResponseEntity.ok(guestService.editGuest(id, dto));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
        }
    /**
     *  Eliminar un huesped del sistema
     * @param id Identificador único del huesped
     * @param dto objeto con los datos a eliminar
     * @return ResponseEntity vacio si la operación es exitosa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id, @RequestBody DeleteGuestDto dto) {
       try {
           guestService.deleteGuest(id, dto);
           return ResponseEntity.noContent().build();
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(null);
       }
    }
    /**
     * Obtener los detalles de un huesped
     * @param id Identificador único del huesped.
     * @return ResponseEntity con el DTO del huesped encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(guestService.getGuestById(id));
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
        return ResponseEntity.ok("¡Funciona la prueba de salida de endpoint!");
    }
}
