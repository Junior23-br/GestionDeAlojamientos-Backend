package com.gestion.alojamientos.dto.transaction.ServiceFee;



public record ServiceFeeUpdateDTO(
        /**
         * Descripcion de la comision
         * Campo obligatorio, por si algun host quiere ver de que se trata el tipo de comison a la que esta sujeto
         */
        String description,
        /**
         * Valor de la comision, ejemplo un 20% o un 30% o lo que sea
         * Campo obligatorio, ya que sin el no se sabria cuanto cobrar
         */
        double value,
        /**
         * Tipo de comision, osea categoria del alojamiento
         * Campo obligatorio, para poder luego filtrar por categorias
         */
        String typeFee,
        /**
         * Calificacion promedio que deben de tener los alojamientos para poder acceder a ella
         * Campo obligatorio, para poder tener un dato de validacion
         */
        double promCalification,
        /**
         * Cantidad de reservas minimas que deben de tener los alojamientos para poder acceder a ella
         * Campo obligatorio, para poder tener un dato de validacion
         */
        Integer numberBookingsMinimum

) {
}
