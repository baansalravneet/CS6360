package com.librarysystem.db.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "borrowers")
public class StoredBorrower {
    @Id
    private final String cardId;
    @Column(unique = true)
    private final String ssn;
    private final String name;
    private final String address;
    private final String phone;

    public StoredBorrower(String cardId, String ssn, String name, String address, String phone) {
        this.cardId = cardId;
        this.ssn = ssn;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getCardId() {
        return cardId;
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
