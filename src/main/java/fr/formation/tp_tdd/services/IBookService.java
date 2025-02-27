package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.models.Book;

public interface IBookService {
    Book addBook(Book book);
    Book updateBook(String isbn, Book book);
    Book fetchBookInfoFromWebService(String isbn);
}