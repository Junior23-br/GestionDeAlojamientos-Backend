package com.gestion.alojamientos.exception;
//Esta clase personalizada lanzará excepcion en elementos nulos
public class NullElementException extends Exception {
    public NullElementException(String message){super(message);}
}
