package fr.formation.tp_tdd.exceptions;

public class InvalidIsbnCharacterException extends RuntimeException {
    public InvalidIsbnCharacterException(String message) {
        super(message);
    }
}
