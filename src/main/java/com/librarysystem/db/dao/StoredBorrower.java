package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "BORROWER")
public class StoredBorrower {
    @Id
    @Column(name = "Card_id")
    private String cardId;
    @Column(unique = true, name = "Ssn", nullable = false)
    private String ssn;
    @Column(name = "Bname")
    private String name;
    @Column(name = "Email")
    private String email;
    @Column(name = "Address")
    private String address;
    @Column(name = "State")
    private String state;
    @Column(name = "City")
    private String city;
    @Column(name = "Phone")
    private String phone;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "borrower")
    private List<StoredLoan> loans;

    public StoredBorrower() {
    }

    public StoredBorrower(String cardId, String ssn, String name, String email, String address, String state, String city, String phone) {
        this.cardId = cardId;
        this.ssn = ssn;
        this.name = name;
        this.email = email;
        this.address = address;
        this.state = state;
        this.city = city;
        this.phone = phone;
    }

    public String getCardId() {
        return cardId;
    }

    public String getSsn() {
        return ssn;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public List<StoredLoan> getLoans() {
        return loans;
    }

    @Override
    public boolean equals(Object a) {
        if (a == null || a.getClass() != StoredBorrower.class) return false;
        return this.getCardId().equals(((StoredBorrower) a).getCardId());
    }
}
