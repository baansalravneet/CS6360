package com.librarysystem.models;

public class Fine {
    private final String loanID;
    private final double fineAmount;
    private boolean paid;

    public Fine(String loanID, double fineAmount, boolean paid) {
        this.loanID = loanID;
        this.fineAmount = fineAmount;
        this.paid = paid;
    }

    public String getLoanID() {
        return loanID;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
