package com.librarysystem.services;

import com.librarysystem.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Autowired
    private DatabaseService databaseService;


    public Book addBook(final Book book) {
        Optional<Book> savedBook = databaseService.getBookById(book.getIsbn());
        return savedBook.orElseGet(() -> databaseService.saveBook(book));
    }

    public boolean addBooks(final List<Book> books) {
        books.forEach(this::addBook);
        return true;
    }

    public List<Book> getBooksForSearchQuery(String searchQuery) {
        List<Book> result = new ArrayList<>();
        result.addAll(databaseService.getBooksByTitle(searchQuery));
        result.addAll(databaseService.getBooksByAuthors(searchQuery));
        result.addAll(databaseService.getBookByIsbn(searchQuery));
        return result.stream().distinct().collect(Collectors.toList());
    }
}