package com.librarysystem.resources;

import com.librarysystem.models.Book;
import com.librarysystem.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookResource {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/addBook")
    public Book addBook(@RequestBody Book book) {
        return databaseService.addBook(book);
    }

    @PostMapping("/addBooks")
    public boolean addBooks(@RequestBody List<Book> books) throws InterruptedException {
        return databaseService.addBooks(books);
    }
}
