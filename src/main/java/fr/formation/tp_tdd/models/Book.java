package fr.formation.tp_tdd.models;

import fr.formation.tp_tdd.enums.Format;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Book {

    @Id
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    @Enumerated(EnumType.STRING)
    private Format format;
    private boolean isAvailable;
}