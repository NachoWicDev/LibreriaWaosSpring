package gm.libreria_waos_spring.exception;

public class ValidacionException extends BaseException {
    public ValidacionException(String message) {
        super("Error de validación: " + message);
    }
}
