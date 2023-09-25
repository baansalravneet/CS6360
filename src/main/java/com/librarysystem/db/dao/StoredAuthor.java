package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "authors")
public class StoredAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final String id;
    private final String name;
    @ManyToMany
    @JoinTable(
            name = "authors_books",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private final Set<StoredBook> books;

    public StoredAuthor(String id, String name, Set<StoredBook> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<StoredBook> getBooks() {
        return books;
    }
}
