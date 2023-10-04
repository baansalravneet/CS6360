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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    static Book toBook(StoredBook sb) {
        return new Book(sb.getIsbn(), sb.getTitle(), sb.getCoverUrl(), sb.getPublisher(), sb.getPages(),
                DatabaseService.toAuthors(sb.getAuthors()), sb.isAvailable());
    }

    private static Set<Author> toAuthors(Set<StoredAuthor> storedAuthors) {
        return storedAuthors.stream().map(DatabaseService::toAuthor).collect(Collectors.toSet());
    }

    @Transactional
    Book saveBook(Book book) {
        StoredBook storedBook = DatabaseService.toStoredBook(book);
        Set<StoredAuthor> savedAuthors = getOrCreateAuthors(book.getAuthors());
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
                null, book.isAvailable());
    }

    private Set<StoredAuthor> getOrCreateAuthors(Set<Author> authors) {
        return authors.stream().map(a -> getOrCreateAuthor(a.getName()))
                .collect(Collectors.toSet());
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

}