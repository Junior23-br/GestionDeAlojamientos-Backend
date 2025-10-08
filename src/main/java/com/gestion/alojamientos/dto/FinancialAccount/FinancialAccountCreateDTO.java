package com.gestion.alojamientos.dto.FinancialAccount;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record FinancialAccountCreateDTO(

        /**
         * Identificador del usuario que esta creando la cuenta financiera
         * Campo obligatorio
         */
        @NotNull @NotEmpty
        Long idUser,

        /**
         * Cantidad de dinero disponible en la cuenta a crear
         * Campo oobligatoria
         */
        @NotNull @NotEmpty
        Double avalaibleBalance,
        /**
         * Nombre del banco asociado a la cuenta financiera
         * Campo obligatorio
         */
        @NotNull @NotEmpty
        String bankName,

        /**
         * Numero de cuenta de la cuenta financiera
         * Campo obligatorio
         */
        @NotNull @NotEmpty
        String numberAccount


) {
}
