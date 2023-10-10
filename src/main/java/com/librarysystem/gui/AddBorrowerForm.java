package com.librarysystem.gui;

import com.librarysystem.services.DatabaseService;

import javax.swing.*;
import java.awt.*;

public class AddBorrowerForm extends JFrame {

    private DatabaseService databaseService;

    public AddBorrowerForm(DatabaseService databaseService) {
        this.databaseService = databaseService;
        configure();
    }

    private void configure() {
        this.setTitle("Registration Form");
        this.setBounds(0, 0, 900, 600);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);

        Container content = this.getContentPane();
        content.setLayout(null);

        JLabel ssn = new JLabel("SSN");
        ssn.setBounds(30, 30, 100, 20);
        content.add(ssn);

        JTextField ssnInput = new JTextField();
        ssnInput.setBounds(ssn.getX() + 100, ssn.getY(), 190, 20);
        content.add(ssnInput);

        JLabel firstName = new JLabel("First Name");
        firstName.setBounds(ssn.getX(), ssn.getY() + 25, 100, 20);
        content.add(firstName);

        JTextField firstNameInput = new JTextField();
        firstNameInput.setBounds(firstName.getX() + 100, firstName.getY(), 190, 20);
        content.add(firstNameInput);

        JLabel lastName = new JLabel("Last Name");
        lastName.setBounds(firstName.getX(), firstName.getY() + 25, 100, 20);
        content.add(lastName);

        JTextField lastNameInput = new JTextField();
        lastNameInput.setBounds(lastName.getX() + 100, lastName.getY(), 190, 20);
        content.add(lastNameInput);

        JLabel email = new JLabel("Email");
        email.setBounds(lastName.getX(), lastName.getY() + 25, 100, 20);
        content.add(email);

        JTextField emailInput = new JTextField();
        emailInput.setBounds(email.getX() + 100, email.getY(), 190, 20);
        content.add(emailInput);

        JLabel address = new JLabel("Address");
        address.setBounds(email.getX(), email.getY() + 25, 100, 26);
        content.add(address);

        JTextArea addressInput = new JTextArea();
        addressInput.setBounds(address.getX() + 105, address.getY(), 180, 60);
        addressInput.setLineWrap(true);
        content.add(addressInput);

        JLabel city = new JLabel("City");
        city.setBounds(address.getX(), address.getY() + 25 + 40, 100, 20);
        content.add(city);

        JTextField cityInput = new JTextField();
        cityInput.setBounds(city.getX() + 100, city.getY(), 190, 20);
        content.add(cityInput);

        JLabel state = new JLabel("State");
        state.setBounds(city.getX(), city.getY() + 25, 100, 20);
        content.add(state);

        JTextField stateInput = new JTextField();
        stateInput.setBounds(state.getX() + 100, state.getY(), 190, 20);
        content.add(stateInput);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(state.getX(), state.getY() + 25, 100, 20);
        content.add(phone);

        JTextField phoneInput = new JTextField();
        phoneInput.setBounds(phone.getX() + 100, phone.getY(), 190, 20);
        content.add(phoneInput);

        JButton addButton = new JButton("Register");
        addButton.setBounds(phone.getX() + 40, phone.getY() + 50, 100, 20);
        content.add(addButton);

        addButton.addActionListener(listener -> {
            String _ssn = ssnInput.getText();
            String _firstName = firstNameInput.getText();
            String _lastName = lastNameInput.getText();
            String _email = emailInput.getText();
            String _address = addressInput.getText();
            String _city = cityInput.getText();
            String _state = stateInput.getText();
            String _phone = phoneInput.getText();

            if (!validSsn(_ssn)) MainWindow.showErrorFrame();
            else if (_firstName.length() == 0
                    || _lastName.length() == 0
                    || _email.length() == 0
                    || _address.length() == 0
                    || _city.length() == 0
                    || _state.length() == 0
                    || _phone.length() == 0) MainWindow.showErrorFrame();
                // TODO: make this into a request object
                // TODO: return a proper error message
            else {
                if (databaseService.registerBorrower(_ssn, _firstName, _lastName,
                        _email, _address, _city, _state, _phone)) MainWindow.showSuccessFrame();
                else MainWindow.showErrorFrame();
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(addButton.getX() + 110, addButton.getY(), 100, 20);
        content.add(resetButton);

        resetButton.addActionListener(listener -> {
            ssnInput.setText("");
            firstNameInput.setText("");
            lastNameInput.setText("");
            emailInput.setText("");
            addressInput.setText("");
            cityInput.setText("");
            stateInput.setText("");
            phoneInput.setText("");
        });

        this.setVisible(true);
    }

    private boolean validSsn(String ssn) {
        return ssn.matches("^\\d{3}-\\d{2}-\\d{4}$");
    }
}
