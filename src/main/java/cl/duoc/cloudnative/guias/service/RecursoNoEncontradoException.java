package cl.duoc.cloudnative.guias.service;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String message) {
        super(message);
    }
}
