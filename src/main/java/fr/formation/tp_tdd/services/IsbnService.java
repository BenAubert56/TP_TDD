package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.exceptions.InvalidIsbnCharacterException;
import fr.formation.tp_tdd.exceptions.InvalidIsbnLengthException;

public class IsbnService {

    public boolean validateIsbn(String isbn) {
        if (isbn.length() != 10)
            throw new InvalidIsbnLengthException("Longueur de l'ISBN non valide");

        int total = 0;
        for (int i = 0; i < 10; i++) {
            if (!Character.isDigit(isbn.charAt(i))) {
                if (i == 9 && isbn.charAt(9) == 'X') {
                    total += 10;
                    break;
                } else {
                    throw new InvalidIsbnCharacterException("Caractère invalide dans l'ISBN");
                }
            }
            total += Character.getNumericValue(isbn.charAt(i)) * (10 - i);
        }
        return total % 11 == 0;
    }

}
