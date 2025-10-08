package com.gestion.alojamientos.dto.FinancialAccount;

import jakarta.validation.constraints.NotNull;

public record FinancialAccountDTO(

        /**
         * Identificador de la cuenta financiera
         * Campo obligatorio
         */
        @NotNull
        Long id,

        /**
         * Nombre del banco de la cuenta financiera
         * Campo obligatorio
         */

        @NotNull
        String bankName,


        /**
         * Numero de cuenta asociado a la cuenta financiera
         * Campo obligatorio
         */
        @NotNull
        String numberAccount,

        /**
         * Identificador del usuario al que esta asociado la cuenta
         * Campo obligatorio
         */
        @NotNull
        Long idUser,


        /**
         * Cantidad de dinero disponible en la cuenta
         * Campo obligatorio
         */
        @NotNull
        Double avalaibleBalance

) {
}
