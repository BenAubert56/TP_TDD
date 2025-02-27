package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.exceptions.*;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.models.Member;
import fr.formation.tp_tdd.models.Reservation;
import fr.formation.tp_tdd.repositories.BookRepository;
import fr.formation.tp_tdd.repositories.MemberRepository;
import fr.formation.tp_tdd.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService implements IReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository, BookRepository bookRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Reservation createReservation(String memberCode, String bookIsbn, LocalDate reservationDate, LocalDate expirationDate) {
        Member member = memberRepository.findByCode(memberCode)
                .orElseThrow(() -> new MemberNotFoundException("Adhérent non trouvé"));

        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new BookNotFoundException("Livre non trouvé"));

        List<Reservation> activeReservations = reservationRepository.findByMemberAndActiveTrue(member);
        if (activeReservations.size() >= 3) {
            throw new MaxReservationsExceededException("L'adhérent a déjà 3 réservations actives");
        }

        if (expirationDate.isAfter(reservationDate.plusMonths(4))) {
            throw new InvalidReservationDateException("La date d'expiration dépasse la limite de 4 mois.");
        }

        Reservation reservation = new Reservation(null, member, book, reservationDate, expirationDate, true);

        return reservationRepository.save(reservation);
    }

    public void endReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Réservation non trouvée"));

        reservation.setActive(false);
        reservationRepository.save(reservation);
    }
}