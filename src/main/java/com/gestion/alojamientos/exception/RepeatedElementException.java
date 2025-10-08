package com.gestion.alojamientos.exception;
//Esta clase se encargará de representar errores cuando un elemento ya existe en el sistema
public class RepeatedElementException extends Throwable {
    public RepeatedElementException(String message) {
        super(message); // Llama al constructor de la clase Exception con el mensaje proporcionado
    }
}
