package fr.formation.tp_tdd.exceptions;

public class MaxReservationsExceededException extends RuntimeException {
    public MaxReservationsExceededException(String message) {
        super(message);
    }
}
