package fr.formation.tp_tdd.exceptions;

public class InvalidIsbnLengthException extends RuntimeException {
    public InvalidIsbnLengthException(String message) {
        super(message);
    }
}
