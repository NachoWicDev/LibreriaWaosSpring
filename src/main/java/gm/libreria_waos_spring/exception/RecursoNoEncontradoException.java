package gm.libreria_waos_spring.exception;

public class RecursoNoEncontradoException extends BaseException {
    public RecursoNoEncontradoException(String message) {
        super("Recurso no encontrado: " + message);
    }
}
