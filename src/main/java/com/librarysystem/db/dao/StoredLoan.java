package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "LOANS")
public class StoredLoan {
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE,
            optional = false)
    @JoinColumn(name = "Isbn")
    private StoredBook book;
    @ManyToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE,
            optional = false)
    @JoinColumn(name = "Card_id")
    private StoredBorrower borrower;
    @OneToOne(fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE,
            mappedBy = "loan")
    private StoredFine fine;
    @Column(name = "Date_out")
    private Date dateOut;
    @Column(name = "Due_date")
    private Date dueDate;
    @Column(name = "Date_in")
    private Date dateIn;

    public StoredLoan() {
    }

    public StoredLoan(Long id, StoredBook book, StoredBorrower borrower, Date dateOut, Date dueDate, Date dateIn, StoredFine fine) {
        this.id = id;
        this.book = book;
        this.borrower = borrower;
        this.dateOut = dateOut;
        this.dueDate = dueDate;
        this.dateIn = dateIn;
        this.fine = fine;
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

    public Date getDateOut() {
        return dateOut;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public StoredFine getFine() {
        return fine;
    }

    @Override
    public boolean equals(Object loan) {
        if (loan == null || loan.getClass() != StoredLoan.class) return false;
        return this.id.longValue() == ((StoredLoan) loan).getId().longValue();
    }

    public String[] displayString() {
        return new String[]{
                String.valueOf(id),
                book.getIsbn(),
                book.getTitle(),
                borrower.getCardId(),
                borrower.getFirstName() + " " + borrower.getLastName(),
                dateOut.toString(),
                dueDate.toString(),
                dateIn == null ? "" : dateIn.toString()
        };
    }

    public String getLoanInfoString() {
        return String.format
                (
                        "ID: %d\n" +
                        "ISBN: %s\n" +
                        "Title: %s\n" +
                        "Borrower ID: %s\n" +
                        "Borrower Name: %s\n" +
                        "Checkout Date: %s\n" +
                        "Due Date: %s\n" +
                        "Checkin Date: %s",

                        id,
                        book.getIsbn(),
                        book.getTitle(),
                        borrower.getCardId(),
                        borrower.getFirstName() + " " + borrower.getLastName(),
                        dateOut.toString(),
                        dueDate.toString(),
                        dateIn == null ? "" : dateIn.toString()
                );
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }

    public void setFine(StoredFine fine) {
        this.fine = fine;
    }
}
