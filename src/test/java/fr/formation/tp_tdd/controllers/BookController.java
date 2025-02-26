package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.enums.Format;
import fr.formation.tp_tdd.exceptions.BookNotFoundException;
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

    @Test
    public void testUpdateBookOk() {
        Book updatedBook = new Book("9781234567890", "TDD - Updated", "Benjamin Aubert", "Aubert Library", Format.BD, false);
        when(bookService.updateBook(eq("9781234567890"), any())).thenReturn(updatedBook);

        ResponseEntity<Book> response = controller.updateBook("9781234567890", updatedBook);

        assertEquals(null, "200 OK", response.getStatusCode().toString());
        assertEquals(null, Format.BD, response.getBody().getFormat());
        verify(bookService, times(1)).updateBook(eq("9781234567890"), any());
    }

    @Test
    public void testUpdateBookKo() {
        Book updatedBook = new Book("9781234567890", "TDD - Updated", "Benjamin Aubert", "Aubert Library", Format.BD, false);
        when(bookService.updateBook(eq("9781234567890"), any())).thenThrow(new BookNotFoundException("Book not found"));

        ResponseEntity<Book> response = controller.updateBook("9781234567890", updatedBook);

        assertEquals(null, "404 NOT_FOUND", response.getStatusCode().toString());
        verify(bookService, times(1)).updateBook(eq("9781234567890"), any());
    }

}