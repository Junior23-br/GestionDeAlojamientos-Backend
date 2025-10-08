package com.gestion.alojamientos.service.Impl;
 import com.gestion.alojamientos.dto.admin.AdminDto;
 import com.gestion.alojamientos.dto.admin.CreateAdminDto;
 import com.gestion.alojamientos.dto.admin.EditAdminDto;
 import com.gestion.alojamientos.exception.ElementNotFoundException;
 import com.gestion.alojamientos.exception.RepeatedElementException;
 import com.gestion.alojamientos.mapper.users.AdminMapper;
 import com.gestion.alojamientos.model.enums.StatesAdmin;
 import com.gestion.alojamientos.model.users.Admin;
 import com.gestion.alojamientos.repository.user.AdminRepository;
 import com.gestion.alojamientos.service.AdminService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.stereotype.Service;

 @Service
 public class AdminServiceImpl implements AdminService {

     @Autowired
     private AdminRepository adminRepository;

     @Autowired
     private AdminMapper adminMapper;

     @Autowired
     private PasswordEncoder passwordEncoder;
     /**
      * Registra un nuevo administrador en el sistema.
      *Nota: Si deseamos sumarle un estado a la informacion de aldministrador cuando se cree desde aca
      * @param dto DTO con los datos del administrador a registrar.
      * @return DTO del administrador creado.
      * @throws RepeatedElementException Si el correo ya está registrado.
      */
     @Override
     public AdminDto registerAdmin(CreateAdminDto dto) throws RepeatedElementException {
         if (adminRepository.existsByEmail(dto.email())) {
             throw new RepeatedElementException("El correo electrónico ya está registrado");
         }

         Admin admin = adminMapper.toEntity(dto);
         admin.setPassword(passwordEncoder.encode(dto.password())); // encripta contraseña
         adminRepository.save(admin);
         return adminMapper.toDTO(admin);
     }

     /**
      * Edita la contraseña de un administrador existente.
      *
      * @param id  Identificador único del administrador.
      * @param dto DTO con los datos a actualizar.
      * @return DTO del administrador actualizado.
      * @throws ElementNotFoundException Si el administrador no existe.
      */
     @Override
     public AdminDto editAdmin(Long id, EditAdminDto dto) throws ElementNotFoundException {
         Admin admin = adminRepository.findById(id)
                 .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id));

         // Verificar que el correo coincida (por seguridad)
         if (!admin.getEmail().equals(dto.email())) {
             throw new IllegalArgumentException("El correo del DTO no coincide con el del administrador registrado");
         }

         // Actualizar otros campos (aunque aquí solo tienes password, pero sirve si luego agrega mos más)
         adminMapper.updateFromDto(dto, admin);

         // Actualizar y encriptar la nueva contraseña
         admin.setPassword(passwordEncoder.encode(dto.password()));

         // Guardar cambios
         Admin updated = adminRepository.save(admin);

         // Devolver DTO seguro (sin password)
         return adminMapper.toDTO(updated);
     }
     //Esta funcionalidad sirve siempre y cuando le agregemos atributo de estado al admin, por si lo queremos borrar

     /**
      * elimina lógicamente un administrador del sistema.
      * @param id Identificador único del administrador
      * @throws ElementNotFoundException Si el administrador no existe
      */
     @Override
     public void deleteAdmin(Long id) throws ElementNotFoundException {
         Admin admin = adminRepository.findById(id)
                 .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id));
         admin.setStatesAdmin(StatesAdmin.DELETED); // eliminación lógica
         adminRepository.save(admin);
     }
     /**
      * Obtiene un administrador por su identificador único.
      *
      * @param id identificador del administrador
      * @return DTO del administrador obtenido
      * @throws ElementNotFoundException si el administrador no existe
      */
     @Override
     public AdminDto getAdminById(Long id) throws ElementNotFoundException {
         return adminMapper.toDTO(adminRepository.findById(id)
                 .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con ID: " + id)));
     }
     /**
      * Obtiene un administrador por su correo electrónico.
      *
      * @param email correo electrónico del administrador
      * @return DTO del administrador obtenido
      * @throws ElementNotFoundException si el administrador no existe
      */
     @Override
     public AdminDto getAdminByEmail(String email) throws ElementNotFoundException {
         Admin admin = adminRepository.findByEmail(email)
                 .orElseThrow(() -> new ElementNotFoundException("Administrador no encontrado con correo: " + email));
         return adminMapper.toDTO(admin);
     }

 }
