package fr.formation.tp_tdd.controllers;

import org.junit.jupiter.api.BeforeEach;

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

        book = new Book("9781234567890", "TDD in Action", "Fabien Le Bronnec", "OpenAI Press", "PAPERBACK", true);
    }

    @Test
    public void testAddBook() {
        when(bookService.addBook(any(Book.class))).thenReturn(book);

        ResponseEntity<Book> response = controller.addBook(book);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("9781234567890", response.getBody().getIsbn());
        verify(bookService, times(1)).addBook(any(Book.class));
    }
}