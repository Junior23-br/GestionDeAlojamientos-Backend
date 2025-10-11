package com.gestion.alojamientos.controller.Admin;


import com.gestion.alojamientos.dto.JwtResponseDTO;
import com.gestion.alojamientos.dto.UserLoginDTO;
import com.gestion.alojamientos.dto.admin.*;
import com.gestion.alojamientos.dto.password.ChangePasswordDto;
import com.gestion.alojamientos.dto.password.ResetPasswordDto;
import com.gestion.alojamientos.exception.InvalidElementException;
import com.gestion.alojamientos.exception.RepeatedElementException;
import com.gestion.alojamientos.mapper.UserLoginMapper;
import com.gestion.alojamientos.security.jwt.JwtUtil;
import com.gestion.alojamientos.service.AdminService;
import com.gestion.alojamientos.service.Impl.AdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "Administradores", description = "Endpoints para gestión y autenticación de administradores del sistema.")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // --------------------------
    // CREATE ADMIN
    // --------------------------
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo administrador")
    public ResponseEntity<AdminDto> createAdmin(@Valid @RequestBody CreateAdminDto dto) {
        try {
            AdminDto creado = adminService.registerAdmin(dto);
            return ResponseEntity.status(201).body(creado);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (RepeatedElementException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // GET ADMIN BY ID
    // --------------------------
    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable String id) {
        try {
            AdminDto admin = adminService.getAdminById(Long.valueOf(id));
            return ResponseEntity.ok(admin);
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // GET ADMIN BY EMAIL
    // --------------------------
    @GetMapping("/email")
    public ResponseEntity<AdminDto> getAdminByEmail(@RequestParam String email) {
        try {
            AdminDto admin = adminService.getAdminByEmail(email);
            return ResponseEntity.ok(admin);
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // UPDATE ADMIN
    // --------------------------
    @PutMapping("/{id}")
    public ResponseEntity<AdminDto> updateAdmin(@PathVariable String id, @Valid @RequestBody EditAdminDto dto) {
        try {
            AdminDto actualizado = adminService.editAdmin(Long.valueOf(id), dto);
            return ResponseEntity.ok(actualizado);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // DELETE ADMIN
    // --------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String id) {
        try {
            adminService.deleteAdmin(Long.valueOf(id));
            return ResponseEntity.noContent().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // LOGIN ADMIN
    // --------------------------
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> loginAdmin(@RequestBody UserLoginDTO dto) {
        try {
            AdminDto adminDto = adminService.getAdminByEmail(dto.email());
            UserLoginDTO admin = adminService.login(dto);

            String token = jwtUtil.generateToken(
                    admin.email(),
                    adminDto.id(),
                    "ADMINISTRADOR"
            );

            JwtResponseDTO response = new JwtResponseDTO(
                    token,
                    "Bearer",
                    "ADMINISTRADOR",
                    adminDto.id(),
                    admin.email()
            );

            return ResponseEntity.ok(response);
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // RESET PASSWORD
    // --------------------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {
            adminService.resetPassword(dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // --------------------------
    // CHANGE PASSWORD
    // --------------------------
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto dto, Long idUser) {
        try {
            adminService.changePassword(idUser, dto);
            return ResponseEntity.ok().build();
        } catch (InvalidElementException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
