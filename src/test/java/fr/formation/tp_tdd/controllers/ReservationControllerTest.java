package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.enums.Format;
import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {
    ReservationService reservationService;
    ReservationRepository reservationRepository;
    MemberRepository memberRepository;
    BookRepository bookRepository;

    Member member;
    Book book;
    Reservation res1, res2, res3;

    @BeforeEach
    public void init() {
        reservationRepository = mock(ReservationRepository.class);
        memberRepository = mock(MemberRepository.class);
        bookRepository = mock(BookRepository.class);
        reservationService = new ReservationService(reservationRepository, memberRepository, bookRepository);

        member = new Member(1L, "MEM123", "John", "Doe", LocalDate.of(1990, 5, 10), Gender.MALE);
        book = new Book("9781234567890", "TDD", "Benjamin Aubert", "Aubert Library", Format.GRAND_FORMAT, true);
        res1 = new Reservation(1L, member, book, LocalDate.now().minusDays(10), LocalDate.now().plusMonths(4), true);
        res2 = new Reservation(2L, member, book, LocalDate.now().minusDays(5), LocalDate.now().plusMonths(4), true);
        res3 = new Reservation(3L, member, book, LocalDate.now(), LocalDate.now().plusMonths(4), true);
    }

    @Test
    public void testCreateReservationSuccess() {
        when(memberRepository.findByCode("MEM123")).thenReturn(member);
        when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(book));
        when(reservationRepository.findByMemberAndActiveTrue(member)).thenReturn(List.of());
        when(reservationRepository.save(any())).thenReturn(new Reservation(1L, member, book, LocalDate.now(), LocalDate.now().plusMonths(4), true));

        Reservation reservation = reservationService.createReservation("MEM123", "9781234567890");

        assertNotNull(reservation);
        assertEquals(member, reservation.getMember());
        assertEquals(book, reservation.getBook());
        assertEquals(LocalDate.now().plusMonths(4), reservation.getExpirationDate());
        assertTrue(reservation.isActive());

        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    public void testCreateReservationMemberHasMaxReservations() {
        when(memberRepository.findByCode("MEM123")).thenReturn(member);
        when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(book));
        when(reservationRepository.findByMemberAndActiveTrue(member)).thenReturn(List.of(res1, res2, res3));

        assertThrows(MaxReservationsExceededException.class, () -> reservationService.createReservation("MEM123", "9781234567890"));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    public void testCreateReservationBookNotFound() {
        when(memberRepository.findByCode("MEM123")).thenReturn(member);
        when(bookRepository.findById("9781234567890")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> reservationService.createReservation("MEM123", "9781234567890"));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    public void testCreateReservationMemberNotFound() {
        when(memberRepository.findByCode("MEM123")).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> reservationService.createReservation("MEM123", "9781234567890"));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    public void testCreateReservationExpirationDateExceedsLimit() {
        when(memberRepository.findByCode("MEM123")).thenReturn(member);
        when(bookRepository.findById("9781234567890")).thenReturn(Optional.of(book));
        when(reservationRepository.findByMemberAndActiveTrue(member)).thenReturn(List.of());

        Reservation reservation = reservationService.createReservation("MEM123", "9781234567890");

        assertNotNull(reservation);
        assertEquals(LocalDate.now().plusMonths(4), reservation.getExpirationDate());
    }

}
