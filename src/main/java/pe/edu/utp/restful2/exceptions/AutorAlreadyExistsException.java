package pe.edu.utp.restful2.exceptions;

public class AutorAlreadyExistsException extends RuntimeException {
    public AutorAlreadyExistsException(String message) {
        super(message);
    }
}
