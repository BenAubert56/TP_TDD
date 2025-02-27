package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.exceptions.InvalidReservationDateException;
import fr.formation.tp_tdd.exceptions.MaxReservationsExceededException;
import fr.formation.tp_tdd.exceptions.MemberNotFoundException;
import fr.formation.tp_tdd.models.Reservation;
import fr.formation.tp_tdd.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{memberCode}/{bookIsbn}")
    public ResponseEntity<?> createReservation(@PathVariable String memberCode, @PathVariable String bookIsbn, @PathVariable LocalDate reservationDate, @PathVariable LocalDate expirationDate) {
        try {
            Reservation reservation = reservationService.createReservation(memberCode, bookIsbn, reservationDate, expirationDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (MemberNotFoundException | BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MaxReservationsExceededException | InvalidReservationDateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}