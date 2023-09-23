package com.librarysystem.services;

import com.librarysystem.db.dao.StoredBook;
import com.librarysystem.db.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<StoredBook> list() {
        return bookRepository.findAll();
    }
}