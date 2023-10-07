package com.librarysystem.db.dao;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "BORROWERS")
public class StoredBorrower {
    @Id
    @Column(name = "Card_id")
    private String cardId;
    @Column(unique = true, name = "Ssn", nullable = false)
    private String ssn;
    @Column(name = "Fname")
    private String firstName;
    @Column(name = "Lname")
    private String lastName;
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

    public StoredBorrower(String cardId, String ssn, String firstName, String lastName, String email, String address, String state, String city, String phone) {
        this.cardId = cardId;
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<StoredLoan> getLoans() {
        return loans;
    }
}
