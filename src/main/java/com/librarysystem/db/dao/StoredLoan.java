package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "LOANS")
public class StoredLoan {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            optional = false)
    @JoinColumn(name = "Isbn")
    private StoredBook book;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            optional = false)
    @JoinColumn(name = "Card_id")
    private StoredBorrower borrower;
    @Column(name = "Date_out")
    private Timestamp dateOut;
    @Column(name = "Due_date")
    private Timestamp dueDate;
    @Column(name = "Date_in")
    private Timestamp dateIn;

    public StoredLoan() {}

    public StoredLoan(Long id, StoredBook book, StoredBorrower borrower, Timestamp dateOut, Timestamp dueDate, Timestamp dateIn) {
        this.id = id;
        this.book = book;
        this.borrower = borrower;
        this.dateOut = dateOut;
        this.dueDate = dueDate;
        this.dateIn = dateIn;
    }

    public Long getId() {
        return id;
    }

    public StoredBook getBook() {
        return book;
    }

    public StoredBorrower getBorrower() {
        return borrower;
    }

    public Timestamp getDateOut() {
        return dateOut;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public Timestamp getDateIn() {
        return dateIn;
    }
}
