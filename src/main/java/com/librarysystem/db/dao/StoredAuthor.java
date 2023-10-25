package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "AUTHORS")
public class StoredAuthor {
    @Id
    @Column(name = "Author_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Name")
    private String name;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "BOOK_AUTHORS",
            joinColumns = @JoinColumn(name = "Author_id"),
            inverseJoinColumns = @JoinColumn(name = "Isbn")
    )
    private List<StoredBook> books = new ArrayList<>();

    public StoredAuthor(Long id, String name, Set<StoredBook> books) {
        this.id = id;
        this.name = name;
        if (books != null) this.books.addAll(books);
    }

    public StoredAuthor() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StoredBook> getBooks() {
        return books;
    }

}
