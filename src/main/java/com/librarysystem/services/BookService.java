package com.librarysystem.services;

import com.librarysystem.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    DatabaseService databaseService;


    public Book addBook(final Book book) {
        Optional<Book> savedBook = databaseService.getBookById(book.getIsbn());
        return savedBook.orElseGet(() -> databaseService.saveBook(book));
    }

    public boolean addBooks(final List<Book> books) {
        books.forEach(this::addBook);
        return true;
    }
}