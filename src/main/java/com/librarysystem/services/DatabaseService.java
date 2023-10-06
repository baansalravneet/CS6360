package com.librarysystem.services;

import com.librarysystem.db.dao.StoredAuthor;
import com.librarysystem.db.dao.StoredBook;
import com.librarysystem.db.repositories.AuthorRepository;
import com.librarysystem.db.repositories.BookRepository;
import com.librarysystem.db.repositories.BorrowerRepository;
import com.librarysystem.models.Author;
import com.librarysystem.models.Book;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BorrowerRepository borrowerRepository;

    Optional<Book> getBookById(String isbn) {
        Optional<StoredBook> savedBook = bookRepository.findById(isbn);
        return savedBook.map(DatabaseService::toBook);
    }

    // TODO: populate this with borrowers
    static Book toBook(StoredBook sb) {
        return new Book(sb.getIsbn(), sb.getTitle(), sb.getCoverUrl(), sb.getPublisher(), sb.getPages(),
                toAuthors(sb.getAuthors()), sb.isAvailable(), null);
    }

    private static List<Author> toAuthors(List<StoredAuthor> storedAuthors) {
        return storedAuthors.stream().map(DatabaseService::toAuthor).collect(Collectors.toList());
    }

    @Transactional
    Book saveBook(Book book) {
        StoredBook storedBook = DatabaseService.toStoredBook(book);
        List<StoredAuthor> savedAuthors = getOrCreateAuthors(book.getAuthors());
        storedBook.setAuthors(savedAuthors);
        savedAuthors.forEach(sa -> {
            checkAndAddBook(sa, storedBook);
            authorRepository.save(sa);
        });
        return DatabaseService.toBook(bookRepository.save(storedBook));
    }

    private static void checkAndAddBook(StoredAuthor sa, StoredBook sb) {
        if (sa.getBooks().stream().noneMatch(book -> book.getIsbn().equals(sb.getIsbn()))) {
            sa.getBooks().add(sb);
        }
    }

    private static StoredBook toStoredBook(Book book) {
        return new StoredBook(book.getIsbn(), book.getTitle(),
                book.getCoverUrl(), book.getPublisher(), book.getPages(),
                null, null, book.isAvailable());
    }

    private List<StoredAuthor> getOrCreateAuthors(List<Author> authors) {
        return authors.stream().map(a -> getOrCreateAuthor(a.getName()))
                .collect(Collectors.toList());
    }

    private StoredAuthor getOrCreateAuthor(String name) {
        Optional<StoredAuthor> savedAuthor = authorRepository.getAuthorByName(name);
        return savedAuthor.orElseGet(() -> new StoredAuthor(null, name, null));
    }

    private static Author toAuthor(StoredAuthor sa) {
        return new Author(sa.getId(), sa.getName());
    }

    public List<Book> getBookByIsbn(String searchQuery) {
        List<StoredBook> matchingBooks = bookRepository.getBooksMatchingId(searchQuery);
        return matchingBooks.stream().map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public Optional<Book> getBookByExactIsbn(String isbn) {
        return bookRepository.getBookByIsbn(isbn).map(DatabaseService::toBook);
    }

    public List<Book> getBooksByTitle(String searchQuery) {
        List<StoredBook> matchingBooks = bookRepository.getBooksMatchingTitle(searchQuery);
        return matchingBooks.stream().map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthors(String searchQuery) {
        List<StoredAuthor> matchingAuthors = authorRepository.getAuthorsMatchingName(searchQuery);
        return matchingAuthors.stream().map(StoredAuthor::getBooks).flatMap(Collection::stream)
                .map(DatabaseService::toBook).collect(Collectors.toList());
    }

    public Book addBook(final Book book) {
        Optional<Book> savedBook = getBookById(book.getIsbn());
        return savedBook.orElseGet(() -> saveBook(book));
    }

    public boolean addBooks(final List<Book> books) {
        books.forEach(this::addBook);
        return true;
    }

    // TODO: Put these in a single query.
    public List<Book> getBooksForSearchQuery(String searchQuery) {
        List<Book> result = new ArrayList<>();
        result.addAll(getBooksByTitle(searchQuery));
        result.addAll(getBooksByAuthors(searchQuery));
        result.addAll(getBookByIsbn(searchQuery));
        return result.stream().distinct().collect(Collectors.toList());
    }

    // TODO: batch update?
    public boolean checkout(List<String> selectedISBN, String borrowerId) {
        List<Book> books = selectedISBN.stream()
                .map(this::getBookByExactIsbn)
                .filter(Optional::isPresent)
                .filter(b -> b.get().isAvailable())
                .map(Optional::get)
                .toList();
        if (books.size() != selectedISBN.size()) {
            return false;
        }
        books.forEach(b -> {
            b.setAvailable(false);
            saveBook(b);
        });
        return true;
    }

}