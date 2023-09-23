package com.librarysystem.models;

public class Borrower {
    private final String cardID;
    private final String ssn;
    private final String name;
    private final String address;
    private final String phone;

    public Borrower(String cardID, String ssn, String name, String address, String phone) {
        this.cardID = cardID;
        this.ssn = ssn;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getCardID() {
        return cardID;
    }

    public String getSsn() {
        return ssn;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
