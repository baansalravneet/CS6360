package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "FINES")
public class StoredFine {
    @Id
    @OneToOne
    @JoinColumn(name = "Loan_id")
    private StoredLoan loan;
    @Column(name = "Fine_amt", columnDefinition = "DECIMAL(10,2)")
    private Double fineAmount;
    @Column(name = "Paid")
    private boolean paid;

    public StoredFine() {}

    public StoredFine(StoredLoan loan, Double fineAmount, boolean paid) {
        this.loan = loan;
        this.fineAmount = fineAmount;
        this.paid = paid;
    }

    public void updateFine(Date dateIn, Date dueDate) {
        if (dateIn == null) dateIn = new Date(System.currentTimeMillis());
        this.fineAmount = 0.25 * dayDifference(dateIn.getTime(), dueDate.getTime());
    }

    private static long dayDifference(long a, long b) {
        return (a-b) / (1000 * 60 * 60 * 24);
    }

    public StoredLoan getLoan() {
        return loan;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
