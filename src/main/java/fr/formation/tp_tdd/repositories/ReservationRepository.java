package fr.formation.tp_tdd.repositories;

import fr.formation.tp_tdd.models.Reservation;
import fr.formation.tp_tdd.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMemberAndActiveTrue(Member member);
    List<Reservation> findByMember(Member member);
    List<Reservation> findByExpirationDateBeforeAndActiveTrue(LocalDate date);
    List<Reservation> findByActiveTrue();

}
