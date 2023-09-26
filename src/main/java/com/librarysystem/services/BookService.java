package com.librarysystem.services;

import com.librarysystem.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {
    @Autowired
    DatabaseService databaseService;

    public Book addBook(final Book book) {
        Optional<Book> savedBook = databaseService.getBookById(book.getIsbn());
        return savedBook.orElse(databaseService.saveBook(book));
    }
}