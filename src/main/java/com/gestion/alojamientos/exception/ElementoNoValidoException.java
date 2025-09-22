package com.gestion.alojamientos.exception;
/*
*Excepcion personalizada que se lanza cuando un elemento no es valido
* Esto, según las validaciones aplicadas
* Se puede usar para representar errores de validacion
 */
public class ElementoNoValidoException extends RuntimeException{
   //Crea la instancia con el mensaje personalizado
    public ElementoNoValidoException(String message){
        super(message);
    }
    /*
    *Crea una instancia nueva de la excepcion con un msj y causa asociada
    * cause, causa original que provoco la excepcion
     */
    public ElementoNoValidoException(String message, Throwable cause){
        super(message, cause);
    }
}
