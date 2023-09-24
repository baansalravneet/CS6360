package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "books")
public class StoredBook {
    @Id
    private final String isbn;
    private final String title;
    @ManyToMany(mappedBy = "books")
    private final Set<StoredAuthor> authors;

    public StoredBook(String isbn, String title, Set<StoredAuthor> authors) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
    }

    public String getName() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Set<StoredAuthor> getAuthors() {
        return authors;
    }
}
