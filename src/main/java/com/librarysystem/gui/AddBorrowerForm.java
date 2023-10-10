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
        this.setBounds(0, 0, 350, 400);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        MainWindow.centerFrameOnScreen(this);

        Container content = this.getContentPane();
        content.setLayout(null);

        JLabel ssn = new JLabel("SSN");
        ssn.setBounds(30, 30, 100, 20);
        content.add(ssn);

        JTextField ssnInput = new JTextField();
        ssnInput.setBounds(130, 30, 190, 20);
        content.add(ssnInput);

        JLabel firstName = new JLabel("First Name");
        firstName.setBounds(30, 55, 100, 20);
        content.add(firstName);

        JTextField firstNameInput = new JTextField();
        firstNameInput.setBounds(130, 55, 190, 20);
        content.add(firstNameInput);

        JLabel lastName = new JLabel("Last Name");
        lastName.setBounds(30, 80, 100, 20);
        content.add(lastName);

        JTextField lastNameInput = new JTextField();
        lastNameInput.setBounds(30 + 100, 80, 190, 20);
        content.add(lastNameInput);

        JLabel email = new JLabel("Email");
        email.setBounds(30, 105, 100, 20);
        content.add(email);

        JTextField emailInput = new JTextField();
        emailInput.setBounds(30 + 100, 105, 190, 20);
        content.add(emailInput);

        JLabel address = new JLabel("Address");
        address.setBounds(30, 130, 100, 26);
        content.add(address);

        JTextArea addressInput = new JTextArea();
        addressInput.setBounds(135, 130, 180, 60);
        addressInput.setLineWrap(true);
        content.add(addressInput);

        JLabel city = new JLabel("City");
        city.setBounds(30, 195, 100, 20);
        content.add(city);

        JTextField cityInput = new JTextField();
        cityInput.setBounds(130, 195, 190, 20);
        content.add(cityInput);

        JLabel state = new JLabel("State");
        state.setBounds(30, 220, 100, 20);
        content.add(state);

        JTextField stateInput = new JTextField();
        stateInput.setBounds(130, 220, 190, 20);
        content.add(stateInput);

        JLabel phone = new JLabel("Phone");
        phone.setBounds(30, 245, 100, 20);
        content.add(phone);

        JTextField phoneInput = new JTextField();
        phoneInput.setBounds(130, 245, 190, 20);
        content.add(phoneInput);

        JButton addButton = new JButton("Register");
        addButton.setBounds(70, 295, 100, 20);
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
                    || _phone.length() == 0) {
                MainWindow.showErrorFrame();
            } else {
                // TODO: make this into a request object
                // TODO: return a proper error message
                if (databaseService.registerBorrower(_ssn, _firstName, _lastName,
                        _email, _address, _city, _state, _phone)) MainWindow.showSuccessFrame();
                else MainWindow.showErrorFrame();
            }
        });
        content.add(addButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(180, 295, 100, 20);
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
        content.add(resetButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(125, 325, 100, 20);
        exitButton.addActionListener(listener -> {
            this.dispose();
        });
        content.add(exitButton);

        this.setVisible(true);
    }

    private boolean validSsn(String ssn) {
        return ssn.matches("^\\d{3}-\\d{2}-\\d{4}$");
    }
}
