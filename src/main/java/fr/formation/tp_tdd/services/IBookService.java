package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.models.Book;

import java.util.List;
import java.util.Optional;

public interface IBookService {
    Book addBook(Book book);
    Book updateBook(String isbn, Book book);
    Book fetchBookInfoFromWebService(String isbn);
    void deleteBook(String isbn);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
}