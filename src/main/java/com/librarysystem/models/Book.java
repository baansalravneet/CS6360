package com.librarysystem.models;

import java.util.List;
import java.util.stream.Collectors;

public class Book {
    private final String isbn;
    private final String title;
    private final String coverUrl;
    private final String publisher;
    private final int pages;
    private final List<Author> authors;
    private boolean available;
    private final List<Borrower> borrowers;

    public Book(String isbn, String title, String coverUrl, String publisher, int pages, List<Author> authors, boolean available, List<Borrower> borrowers) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
        this.pages = pages;
        this.authors = authors;
        this.available = available;
        this.borrowers = borrowers;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPages() {
        return pages;
    }

    public String[] displayString() {
        return new String[] { isbn, title, getAuthorsString(), available ? "Yes" : "No" };
    }

    public String getBookInfoString() {
        return String.format("ISBN: %s\nTitle: %s\nAuthors: %s\nPublisher: %s\nPages: %d\nAvailable: %s",
                isbn, title, getAuthorsString(), publisher, pages, available ? "Yes" : "No");
    }

    @Override
    public boolean equals(Object book) {
        if (book.getClass() != Book.class) return false;
        return this.getIsbn().equals(((Book)book).getIsbn());
    }

    private String getAuthorsString() {
        return getAuthors().stream().map(Author::getName).collect(Collectors.joining(","));
    }

    public List<Borrower> getBorrowers() {
        return borrowers;
    }
}
