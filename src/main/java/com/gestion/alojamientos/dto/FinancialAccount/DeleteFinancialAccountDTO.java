package com.gestion.alojamientos.dto.FinancialAccount;

public record DeleteFinancialAccountDTO (
        /**
         * Identificacion del usuario
          */
        Long idUser,

        /**
         * Identificador de la cuenta financiera
         */
        Long idFinancialAccount

) {


}
