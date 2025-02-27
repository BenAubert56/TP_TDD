package fr.formation.tp_tdd.services;

import fr.formation.tp_tdd.exceptions.BookNotFoundException;
import fr.formation.tp_tdd.exceptions.DuplicateBookException;
import fr.formation.tp_tdd.exceptions.InvalidIsbnException;
import fr.formation.tp_tdd.exceptions.MissingBookInformationException;
import fr.formation.tp_tdd.models.Book;
import fr.formation.tp_tdd.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class BookService implements IBookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {
        IsbnService validator = new IsbnService();
        if (!validator.validateIsbn(book.getIsbn())) {
            throw new InvalidIsbnException("ISBN invalide");
        }

        if (book.getTitle() == null || book.getAuthor() == null || book.getPublisher() == null || book.getFormat() == null) {
            book = fetchBookInfoFromWebService(book.getIsbn());
            if (book == null) {
                throw new MissingBookInformationException("Tous les champs doivent être renseignés");
            }
        }

        if (bookRepository.existsById(book.getIsbn())) {
            throw new DuplicateBookException("Le livre existe déjà");
        }

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

    @Override
    public Book fetchBookInfoFromWebService(String isbn) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://book-info-service.com/api/books/" + isbn;

        try {
            return restTemplate.getForObject(url, Book.class);
        } catch (Exception e) {
            return null;
        }
    }
}