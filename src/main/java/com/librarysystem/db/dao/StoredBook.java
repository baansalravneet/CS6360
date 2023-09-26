package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "books")
public class StoredBook {
    @Id
    private final String isbn;
    private final String title;
    @Column(name="cover_url")
    private final String coverUrl;
    private final String publisher;
    private final int pages;
    @ManyToMany(mappedBy = "books")
    private Set<StoredAuthor> authors;
    private final boolean available;

    public StoredBook(String isbn, String title, String coverUrl, String publisher, int pages, Set<StoredAuthor> authors, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
        this.pages = pages;
        this.authors = authors;
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Set<StoredAuthor> getAuthors() {
        return authors;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAuthors(Set<StoredAuthor> authors) {
        this.authors = authors;
    }
}
