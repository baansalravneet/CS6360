package com.librarysystem.db.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BORROWERS")
public class StoredBorrower {
    @Id
    private String cardId;
    @Column(unique = true)
    private String ssn;
    private String name;
    private String address;
    private String phone;

    public StoredBorrower() {}

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
