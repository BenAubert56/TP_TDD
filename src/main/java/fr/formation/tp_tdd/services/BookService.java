package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService implements IBookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }
}