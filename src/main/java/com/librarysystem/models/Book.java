package com.librarysystem.models;

import java.util.List;

public class Book {
    private final String isbn;
    private final String title;
    private final List<Author> authors;
    private boolean available;

    public Book(String isbn, String title, List<Author> authors, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
