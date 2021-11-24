package it.decimo.prenotation_service.exception;

public class AlreadyPrenotedException extends Exception {
    public AlreadyPrenotedException(String message) {
        super(message);
    }
}