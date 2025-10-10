package com.gestion.alojamientos.service.Impl;
import com.gestion.alojamientos.model.base.SuperUser;
import com.gestion.alojamientos.model.common.ResetCode;
import com.gestion.alojamientos.model.users.Admin;
import com.gestion.alojamientos.model.users.Guest;
import com.gestion.alojamientos.service.EmailService;
import com.gestion.alojamientos.service.ResetCodeService;
import com.gestion.alojamientos.exception.InvalidElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ResetCodeServiceImpl implements ResetCodeService {

    @Autowired
    private EmailService emailService;

    @Override
    public String generateAndSendCode(SuperUser guest) throws InvalidElementException {
        // Generar código aleatorio de 7 caracteres
        String code = generateRandomCode(7);
        // Crear ResetCode con expiración en 15 minutos
        ResetCode resetCode = new ResetCode();
        resetCode.setResetCode(code);
        resetCode.setExpirationDate(LocalDateTime.now().plusMinutes(15));
        // Asignar al guest el code
        guest.setResetCode(resetCode);
        // Enviar código por email
        emailService.SendResetCodeEmail(guest.getEmail(), code);

        return code;
    }

    @Override
    public void validateCode(SuperUser guest, String code) throws InvalidElementException {
        if (guest.getResetCode() == null || guest.getResetCode().getResetCode() == null) {
            throw new InvalidElementException("No se ha generado un código de restablecimiento.");
        }
        if (!guest.getResetCode().getResetCode().equals(code)) {
            throw new InvalidElementException("Código de recuperación incorrecto.");
        }
        if (guest.getResetCode().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidElementException("Código de recuperación expirado.");
        }
    }

    // Método para generar código aleatorio
    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }


}