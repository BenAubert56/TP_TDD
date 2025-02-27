package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.exceptions.DuplicateBookException;
import fr.formation.tp_tdd.exceptions.InvalidIsbnException;
import fr.formation.tp_tdd.exceptions.MissingBookInformationException;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.services.BookService;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Setter
@RestController
@RequestMapping("/books")
public class BookController {

    private BookService bookService;

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (InvalidIsbnException | MissingBookInformationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (DuplicateBookException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(isbn, book);
            return ResponseEntity.ok(updatedBook);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}