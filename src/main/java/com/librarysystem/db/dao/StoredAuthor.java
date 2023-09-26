package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "authors")
public class StoredAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;
    private final String name;
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private final Set<StoredBook> books;

    public StoredAuthor(Long id, String name, Set<StoredBook> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<StoredBook> getBooks() {
        return books;
    }

}
