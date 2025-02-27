package fr.formation.tp_tdd.exceptions;

public class MissingBookInformationException extends RuntimeException {
    public MissingBookInformationException(String message) {
        super(message);
    }
}
