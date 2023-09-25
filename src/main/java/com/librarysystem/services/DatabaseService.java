package com.librarysystem.services;

import com.librarysystem.db.repositories.AuthorRepository;
import com.librarysystem.db.repositories.BookRepository;
import com.librarysystem.db.repositories.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private BorrowerRepository borrowerRepository;

}