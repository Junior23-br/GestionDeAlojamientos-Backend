package com.gestion.alojamientos.exception;
//Esta clase personalizada lanzará excepcion en elementos nulos
public class ElementoNuloException extends Exception {
    public ElementoNuloException(String message){super(message);}
}
