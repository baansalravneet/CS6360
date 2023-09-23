package com.librarysystem.models;

import java.util.List;

public class BookLoan {
    private final String loanID;
    private final String isbn;
    private final String cardID;
    private final String dateOut;
    private final String dueDate;
    private final String dateIn;
    private final List<Fine> fines;

    public BookLoan(String loanID,
                    String isbn,
                    String cardID,
                    String dateOut,
                    String dueDate,
                    String dateIn,
                    List<Fine> fines) {
        this.loanID = loanID;
        this.isbn = isbn;
        this.cardID = cardID;
        this.dateOut = dateOut;
        this.dueDate = dueDate;
        this.dateIn = dateIn;
        this.fines = fines;
    }

    public String getLoanID() {
        return loanID;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCardID() {
        return cardID;
    }

    public String getDateOut() {
        return dateOut;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDateIn() {
        return dateIn;
    }

    public List<Fine> getFines() {
        return fines;
    }
}
