package gm.libreria_waos_spring.exception;

public class ConflictoException extends BaseException {
    public ConflictoException(String message) {
        super("Conflicto: " + message);
    }
}
