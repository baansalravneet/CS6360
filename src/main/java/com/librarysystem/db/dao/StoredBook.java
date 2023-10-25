package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BOOK")
public class StoredBook {
    @Id
    @Column(name = "Isbn")
    private String isbn;
    @Column(name = "Title")
    private String title;
    @Column(name = "Cover_url")
    private String coverUrl;
    @Column(name = "Publisher")
    private String publisher;
    @Column(name = "Pages")
    private int pages;
    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<StoredAuthor> authors = new ArrayList<>();
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<StoredLoan> loans;
    @Column(name = "Available")
    private boolean available;

    public StoredBook(String isbn, String title, String coverUrl, String publisher, int pages,
                      List<StoredAuthor> authors, List<StoredLoan> loans, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
        this.publisher = publisher;
        this.pages = pages;
        this.authors = authors;
        this.loans = loans;
        this.available = available;
    }

    public StoredBook() {
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public List<StoredAuthor> getAuthors() {
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

    public void setAuthors(List<StoredAuthor> authors) {
        this.authors = authors;
    }

    public List<StoredLoan> getLoans() {
        return loans;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
