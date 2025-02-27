package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.models.Reservation;

import java.time.LocalDate;

public interface IReservationService {
    Reservation createReservation(String memberCode, String bookIsbn, LocalDate reservationDate, LocalDate expirationDate);
}
