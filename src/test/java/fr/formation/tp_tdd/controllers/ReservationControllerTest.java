package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.exceptions.*;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.models.Member;
import fr.formation.tp_tdd.models.Reservation;
import fr.formation.tp_tdd.enums.Gender;
import fr.formation.tp_tdd.enums.Format;
import fr.formation.tp_tdd.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {
    ReservationController controller;
    ReservationService reservationService;

    Member member;
    Book book;
    Reservation reservation;
    Reservation reservation2;
    Reservation reservation3;

    @BeforeEach
    public void init() {
        // Mock the service
        reservationService = mock(ReservationService.class);
        controller = new ReservationController(reservationService);

        // Create test member, book, and reservation
        member = new Member(1L, "MEM123", "John", "Doe", LocalDate.of(1990, 5, 10), Gender.HOMME);
        book = new Book("9781234567890", "TDD", "Benjamin Aubert", "Aubert Library", Format.GRAND_FORMAT, true);
        reservation = new Reservation(1L, member, book, LocalDate.now(), LocalDate.now().plusMonths(4), true);
        reservation2 = new Reservation(2L, member, book, LocalDate.now().minusDays(5), LocalDate.now().plusMonths(3), true);
        reservation3 = new Reservation(3L, member, book, LocalDate.now(), LocalDate.now().plusMonths(1), true);

    }

    @Test
    public void testCreateReservationSuccess() {
        LocalDate today = LocalDate.now();
        LocalDate expiration = today.plusMonths(3);

        when(reservationService.createReservation("MEM123", "9781234567890", today, expiration))
                .thenReturn(reservation);

        ResponseEntity<?> response = controller.createReservation("MEM123", "9781234567890", today, expiration);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Reservation);
        assertEquals(reservation, response.getBody());

        verify(reservationService, times(1)).createReservation("MEM123", "9781234567890", today, expiration);
    }

    @Test
    public void testCreateReservationMemberHasMaxReservations() {
        LocalDate today = LocalDate.now();
        LocalDate expiration = today.plusMonths(3);

        when(reservationService.createReservation("MEM123", "9781234567890", today, expiration))
                .thenThrow(new MaxReservationsExceededException("L'adhérent a déjà 3 réservations actives"));

        ResponseEntity<?> response = controller.createReservation("MEM123", "9781234567890", today, expiration);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("L'adhérent a déjà 3 réservations actives", response.getBody());
        verify(reservationService, times(1)).createReservation("MEM123", "9781234567890", today, expiration);
    }

    @Test
    public void testCreateReservationBookNotFound() {
        LocalDate today = LocalDate.now();
        LocalDate expiration = today.plusMonths(3);

        when(reservationService.createReservation("MEM123", "9781234567890", today, expiration))
                .thenThrow(new BookNotFoundException("Livre non trouvé"));

        ResponseEntity<?> response = controller.createReservation("MEM123", "9781234567890", today, expiration);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Livre non trouvé", response.getBody());
        verify(reservationService, times(1)).createReservation("MEM123", "9781234567890", today, expiration);
    }

    @Test
    public void testCreateReservationMemberNotFound() {
        LocalDate today = LocalDate.now();
        LocalDate expiration = today.plusMonths(3);

        when(reservationService.createReservation("MEM123", "9781234567890", today, expiration))
                .thenThrow(new MemberNotFoundException("Adhérent non trouvé"));

        ResponseEntity<?> response = controller.createReservation("MEM123", "9781234567890", today, expiration);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Adhérent non trouvé", response.getBody());
        verify(reservationService, times(1)).createReservation("MEM123", "9781234567890", today, expiration);
    }

    @Test
    public void testCreateReservationExpirationDateExceedsLimit() {
        LocalDate today = LocalDate.now();
        LocalDate expiration = today.plusMonths(5); // Exceeds the limit

        when(reservationService.createReservation("MEM123", "9781234567890", today, expiration))
                .thenThrow(new InvalidReservationDateException("La date d'expiration dépasse la limite de 4 mois."));

        ResponseEntity<?> response = controller.createReservation("MEM123", "9781234567890", today, expiration);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("La date d'expiration dépasse la limite de 4 mois.", response.getBody());
        verify(reservationService, times(1)).createReservation("MEM123", "9781234567890", today, expiration);
    }

    @Test
    public void testEndReservationSuccess() {
        doNothing().when(reservationService).endReservation(1L);

        ResponseEntity<?> response = controller.endReservation(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(reservationService, times(1)).endReservation(1L);
    }

    @Test
    public void testEndReservationNotFound() {
        doThrow(new ReservationNotFoundException("Réservation non trouvée")).when(reservationService).endReservation(999L);

        ResponseEntity<?> response = controller.endReservation(999L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Réservation non trouvée", response.getBody());
        verify(reservationService, times(1)).endReservation(999L);
    }

    @Test
    public void testEndAlreadyEndedReservation() {
        doNothing().when(reservationService).endReservation(2L);

        ResponseEntity<?> response = controller.endReservation(2L);

        assertEquals(204, response.getStatusCodeValue());
        verify(reservationService, times(1)).endReservation(2L);
    }

    @Test
    public void testGetOpenReservationsSuccess() {
        when(reservationService.getOpenReservations()).thenReturn(Arrays.asList(reservation, reservation2, reservation3));

        ResponseEntity<List<Reservation>> response = controller.getOpenReservations();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        verify(reservationService, times(1)).getOpenReservations();
    }

    @Test
    public void testGetOpenReservationsEmptyList() {
        when(reservationService.getOpenReservations()).thenReturn(List.of());

        ResponseEntity<List<Reservation>> response = controller.getOpenReservations();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(reservationService, times(1)).getOpenReservations();
    }
}
