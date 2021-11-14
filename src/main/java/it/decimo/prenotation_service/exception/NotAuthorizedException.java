package it.decimo.prenotation_service.exception;

public class NotAuthorizedException extends Exception {

    public NotAuthorizedException(String message) {
        super(message);
    }

    public NotAuthorizedException() {
        super();
    }
}
