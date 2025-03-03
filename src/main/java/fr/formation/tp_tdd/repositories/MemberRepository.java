package fr.formation.tp_tdd.repositories;

import fr.formation.tp_tdd.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByCode(String code);
    Optional<Member> findByCode(String code);
}
