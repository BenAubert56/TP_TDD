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
        IsbnService validator = new IsbnService();
        if (!validator.validateIsbn(book.getIsbn())) {
            throw new InvalidIsbnException("ISBN invalide");
        }

        Optional<Book> existingBookOpt = bookRepository.findById(isbn);
        if (existingBookOpt.isEmpty()) {
            throw new BookNotFoundException("Le livre n'a pas été trouvé");
        }

        Book existingBook = existingBookOpt.get();

        if (!isbn.equals(book.getIsbn())) {
            throw new DuplicateBookException("Le livre existe déjà");
        }

        if (book.getTitle() == null || book.getAuthor() == null || book.getPublisher() == null || book.getFormat() == null) {
            Book fetchedBook = fetchBookInfoFromWebService(isbn);
            if (fetchedBook == null) {
                throw new MissingBookInformationException("Le livre n'a pas été trouvé dans le référentiel");
            }
            if (book.getTitle() == null) book.setTitle(fetchedBook.getTitle());
            if (book.getAuthor() == null) book.setAuthor(fetchedBook.getAuthor());
            if (book.getPublisher() == null) book.setPublisher(fetchedBook.getPublisher());
            if (book.getFormat() == null) book.setFormat(fetchedBook.getFormat());
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setFormat(book.getFormat());
        existingBook.setAvailable(book.isAvailable());

        return bookRepository.save(existingBook);
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