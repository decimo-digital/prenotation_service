package it.decimo.prenotation_service.exception;

public class PrenotationExpiredException extends Exception {
    public PrenotationExpiredException(String message) {
        super(message);
    }
}
