package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.exceptions.*;
import fr.formation.tp_tdd.models.Reservation;
import fr.formation.tp_tdd.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> endReservation(@PathVariable Long reservationId) {
        try {
            reservationService.endReservation(reservationId);
            return ResponseEntity.noContent().build();
        } catch (ReservationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/open")
    public ResponseEntity<List<Reservation>> getOpenReservations() {
        List<Reservation> openReservations = reservationService.getOpenReservations();
        return ResponseEntity.ok(openReservations);
    }

    @GetMapping("/history/{memberCode}")
    public ResponseEntity<List<Reservation>> getReservationHistory(@PathVariable String memberCode) {
        try {
            List<Reservation> history = reservationService.getReservationHistory(memberCode);
            return ResponseEntity.ok(history);
        } catch (MemberNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}