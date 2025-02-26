package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.enums.Format;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

class BookControllerTest {
    BookController controller;
    BookService bookService;
    Book book;

    @BeforeEach
    public void init() {
        controller = new BookController();
        bookService = mock(BookService.class);

        controller.setBookService(bookService);
        book = new Book("9781234567890", "TDD", "Benjamin Aubert", "Aubert Library", Format.GRAND_FORMAT, true);
    }

    @Test
    public void testAddBook() {
        when(bookService.addBook(any(Book.class))).thenReturn(book);

        ResponseEntity<Book> response = controller.addBook(book);

        assertEquals(null, "201 CREATED", response.getStatusCode().toString());
        assertEquals(null, "9781234567890", Objects.requireNonNull(response.getBody()).getIsbn());
        verify(bookService, times(1)).addBook(any(Book.class));
    }
}