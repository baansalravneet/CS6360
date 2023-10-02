package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="BOOKS")
public class StoredBook {
    @Id
    @Column(name = "Isbn")
    private String isbn;
    @Column(name = "Title")
    private String title;
    @Column(name="Cover_url")
    private String coverUrl;
    @Column(name = "Publisher")
    private String publisher;
    @Column(name = "Pages")
    private int pages;
    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
    private Set<StoredAuthor> authors = new HashSet<>();
    @Column(name = "Available")
    private boolean available;

    public StoredBook(String isbn, String title, String coverUrl, String publisher, int pages, Set<StoredAuthor> authors, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
        this.pages = pages;
        this.authors = authors;
        this.available = available;
    }

    public StoredBook() {}

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
