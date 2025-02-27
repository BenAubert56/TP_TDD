package fr.formation.tp_tdd.models;

import fr.formation.tp_tdd.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private Long id;
    private String code; // Unique member code
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}