package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService implements IBookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(String isbn, Book book) {
        Optional<Book> existingBook = bookRepository.findById(isbn);

        if (existingBook.isPresent()) {
            Book updatedBook = existingBook.get();
            updatedBook.setTitle(book.getTitle());
            updatedBook.setAuthor(book.getAuthor());
            updatedBook.setPublisher(book.getPublisher());
            updatedBook.setFormat(book.getFormat());
            updatedBook.setAvailable(book.isAvailable());
            return bookRepository.save(updatedBook);
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }
}