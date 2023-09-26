package com.librarysystem.services;

import com.librarysystem.db.dao.StoredAuthor;
import com.librarysystem.db.dao.StoredBook;
import com.librarysystem.db.repositories.AuthorRepository;
import com.librarysystem.db.repositories.BookRepository;
import com.librarysystem.db.repositories.BorrowerRepository;
import com.librarysystem.models.Author;
import com.librarysystem.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    @Autowired
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
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

    Book saveBook(Book book) {
        StoredBook storedBook = DatabaseService.toStoredBook(book);
        Set<StoredAuthor> savedAuthors = getOrSaveAuthors(book.getAuthors());
        storedBook.setAuthors(savedAuthors);
        savedAuthors.forEach(sa -> sa.getBooks().add(storedBook));
        return DatabaseService.toBook(bookRepository.save(storedBook));
    }

    private static StoredBook toStoredBook(Book book) {
        return new StoredBook(book.getIsbn(), book.getTitle(),
                book.getCoverUrl(), book.getPublisher(), book.getPages(),
                null, book.isAvailable());
    }

    private Set<StoredAuthor> getOrSaveAuthors(Set<Author> authors) {
        return authors.stream().map(a -> getOrSaveAuthor(a.getName()))
                .collect(Collectors.toSet());
    }

    StoredAuthor getOrSaveAuthor(String name) {
        Optional<StoredAuthor> savedAuthor = authorRepository.getAuthorByName(name);
        return savedAuthor.orElseGet(() -> new StoredAuthor(null, name, null));
    }

    private static Author toAuthor(StoredAuthor sa) {
        return new Author(sa.getId(), sa.getName());
    }

}