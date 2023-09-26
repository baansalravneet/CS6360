package com.librarysystem.models;

import java.util.Set;

public class Book {
    private final String isbn;
    private final String title;
    private final String coverUrl;
    private final String publisher;
    private final int pages;
    private final Set<Author> authors;
    private boolean available;

    public Book(String isbn, String title, String coverUrl, String publisher, int pages, Set<Author> authors, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
        this.pages = pages;
        this.authors = authors;
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPages() {
        return pages;
    }
}
