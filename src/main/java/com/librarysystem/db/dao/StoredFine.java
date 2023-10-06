package com.librarysystem.db.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "FINES")
public class StoredFine {
    @OneToOne
    @JoinColumn(name = "Loan_id")
    private StoredLoan loan;
    @Column(name = "Fine_amt")
    private Double fineAmount;
    @Column(name = "Paid")
    private boolean paid;

    public StoredFine() {}

    public StoredFine(StoredLoan loan, Double fineAmount, boolean paid) {
        this.loan = loan;
        this.fineAmount = fineAmount;
        this.paid = paid;
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
}
