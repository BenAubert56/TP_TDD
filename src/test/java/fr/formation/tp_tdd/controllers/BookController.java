package fr.formation.tp_tdd.controllers;

import fr.formation.tp_tdd.enums.Format;
import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.exceptions.DuplicateBookException;
import fr.formation.tp_tdd.exceptions.InvalidIsbnException;
import fr.formation.tp_tdd.exceptions.MissingBookInformationException;
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
    public void testAddBookInvalidIsbn() {
        Book invalidBook = new Book("1234", "Invalid ISBN Book", "Author", "Publisher", Format.GRAND_FORMAT, true);

        when(bookService.addBook(any())).thenThrow(new InvalidIsbnException("ISBN invalide"));

        ResponseEntity<Book> response = controller.addBook(invalidBook);

        assertEquals(null, "400 BAD_REQUEST", response.getStatusCode().toString());
        verify(bookService, times(1)).addBook(any());
    }

    @Test
    public void testAddDuplicateBook() {
        when(bookService.addBook(any())).thenThrow(new DuplicateBookException("Le livre existe déjà"));

        ResponseEntity<Book> response = controller.addBook(book);

        assertEquals(null, "409 CONFLICT", response.getStatusCode().toString());
        verify(bookService, times(1)).addBook(any());
    }

    @Test
    public void testAddBookFetchFromWebService() {
        Book incompleteBook = new Book("9781234567890", null, null, null, null, true);

        when(bookService.fetchBookInfoFromWebService("9781234567890")).thenReturn(book);
        when(bookService.addBook(incompleteBook)).thenReturn(book);

        ResponseEntity<Book> response = controller.addBook(incompleteBook);

        assertEquals(null, "201 CREATED", response.getStatusCode().toString());
        assertEquals(null, "TDD", response.getBody().getTitle());
        verify(bookService, times(1)).addBook(incompleteBook);
    }

    @Test
    public void testAddBookWebServiceFailure() {
        Book incompleteBook = new Book("9781234567890", null, null, null, null, true);

        when(bookService.fetchBookInfoFromWebService("9781234567890")).thenReturn(null);
        when(bookService.addBook(incompleteBook)).thenThrow(new MissingBookInformationException("Le livre n'a pas été trouvé dans le référentiel"));

        ResponseEntity<Book> response = controller.addBook(incompleteBook);

        assertEquals(null, "400 BAD_REQUEST", response.getStatusCode().toString());
        verify(bookService, times(1)).addBook(incompleteBook);
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

    @Test
    public void testUpdateBookInvalidIsbn() {
        Book updatedBook = new Book("1234", "Invalid ISBN Book", "Author", "Publisher", Format.GRAND_FORMAT, false);
        when(bookService.updateBook(eq("1234"), any())).thenThrow(new InvalidIsbnException("ISBN invalide"));

        ResponseEntity<Book> response = controller.updateBook("1234", updatedBook);

        assertEquals(null, "400 BAD_REQUEST", response.getStatusCode().toString());
        verify(bookService, times(1)).updateBook(eq("1234"), any());
    }

    @Test
    public void testUpdateBookDuplicateIsbn() {
        Book updatedBook = new Book("9789876543210", "Updated Book", "Another Author", "Another Publisher", Format.BROCHE, true);

        when(bookService.updateBook(eq("9781234567890"), any()))
                .thenThrow(new DuplicateBookException("Le livre existe déjà"));

        ResponseEntity<Book> response = controller.updateBook("9781234567890", updatedBook);

        assertEquals(null, "409 CONFLICT", response.getStatusCode().toString());
        verify(bookService, times(1)).updateBook(eq("9781234567890"), any());
    }


    @Test
    public void testUpdateBookMissingFields() {
        Book updatedBook = new Book("9781234567890", null, "Author", null, Format.GRAND_FORMAT, false);
        when(bookService.updateBook(eq("9781234567890"), any()))
                .thenThrow(new MissingBookInformationException("Tous les champs doivent être renseignés"));

        ResponseEntity<Book> response = controller.updateBook("9781234567890", updatedBook);

        assertEquals(null, "400 BAD_REQUEST", response.getStatusCode().toString());
        verify(bookService, times(1)).updateBook(eq("9781234567890"), any());
    }

    @Test
    public void testUpdateBookFetchFromWebService() {
        Book incompleteBook = new Book("9781234567890", null, null, null, null, true);

        when(bookService.fetchBookInfoFromWebService("9781234567890")).thenReturn(book);
        when(bookService.updateBook(eq("9781234567890"), any())).thenReturn(book);

        ResponseEntity<Book> response = controller.updateBook("9781234567890", incompleteBook);

        assertEquals(null, "200 OK", response.getStatusCode().toString());
        assertEquals(null,"TDD", response.getBody().getTitle());
        verify(bookService, times(1)).updateBook(eq("9781234567890"), any());
    }

    @Test
    public void testDeleteBook() {
        doNothing().when(bookService).deleteBook("9781234567890");

        ResponseEntity<Void> response = controller.deleteBook("9781234567890");

        assertEquals(null,"204 NO_CONTENT", response.getStatusCode().toString());
        verify(bookService, times(1)).deleteBook("9781234567890");
    }

    @Test
    public void testDeleteBookNotFound() {
        doThrow(new BookNotFoundException("Le livre n'a pas été trouvé")).when(bookService).deleteBook("9781234567890");

        ResponseEntity<Void> response = controller.deleteBook("9781234567890");

        assertEquals(null, "404 NOT_FOUND", response.getStatusCode().toString());
        verify(bookService, times(1)).deleteBook("9781234567890");
    }

}