package cl.duoc.cloudnative.guias.service;

public class ArchivoNoDisponibleException extends RuntimeException {

    public ArchivoNoDisponibleException(String message) {
        super(message);
    }
}
