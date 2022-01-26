package it.decimo.prenotation_service.exception;

public class NotEnoughSeatsException extends Exception {

    public NotEnoughSeatsException(String msg) {
        super(msg);
    }
}
