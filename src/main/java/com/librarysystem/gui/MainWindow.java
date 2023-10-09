package com.librarysystem.gui;

import com.librarysystem.gui.customcomponents.PromptTextField;
import com.librarysystem.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// TODO: Try to make all of the variable local.
@Component
public class MainWindow extends JFrame { // JFrame is the main window of the application

    @Autowired
    private DatabaseService databaseService;

    public MainWindow() {
        super("Library System"); // make a new JFrame
        configure(); // initialise with basic settings
        setSize(750, 750);
    }

    private void configure() {
        this.setLayout(new FlowLayout());
        this.setVisible(true);

        JPanel searchPanel = new JPanel();
        JPanel checkoutPanel = new JPanel();
        JPanel checkinPanel = new JPanel();

        searchPanel.setLayout(new FlowLayout());
        checkoutPanel.setLayout(new FlowLayout());
        checkinPanel.setLayout(new FlowLayout());

        JTextField searchTextInput = new PromptTextField("Search Prompt");
        JButton searchBookButton = new JButton("Search");

        JTextField checkoutTextInput = new PromptTextField("Book ISBN");
        JTextField checkoutBorrowerTextInput = new PromptTextField("Borrower ID");
        JButton checkoutBookButton = new JButton("Checkout");

        JTextField checkinTextInput = new PromptTextField("Checkin Search");
        JButton checkinBookSearchButton = new JButton("Search");

        searchBookButton.addActionListener(listener -> {
            showSearchResultsFrame(searchTextInput.getText());
        });
        checkoutBookButton.addActionListener(listener -> {
            String isbn = checkoutTextInput.getText();
            String borrowerId = checkoutBorrowerTextInput.getText();
            boolean checkedOut = databaseService.checkout(List.of(isbn), borrowerId);
            if (checkedOut) showSuccessFrame();
            else showErrorFrame();
        });
        checkinBookSearchButton.addActionListener(listener -> {
            showCheckinSearchResultsFrame(checkinTextInput.getText());
        });

        searchPanel.add(searchTextInput);
        searchPanel.add(searchBookButton);

        checkoutPanel.add(checkoutTextInput);
        checkoutPanel.add(checkoutBorrowerTextInput);
        checkoutPanel.add(checkoutBookButton);

        checkinPanel.add(checkinTextInput);
        checkinPanel.add(checkinBookSearchButton);

        this.add(searchPanel);
        this.add(checkoutPanel);
        this.add(checkinPanel);
        this.pack();
    }

    private void showCheckinSearchResultsFrame(String searchQuery) {
        new CheckinSearchWindow(searchQuery, databaseService);
    }

    private void showSearchResultsFrame(String searchQuery) {
        new BookSearchWindow(searchQuery, databaseService);
    }

    // TODO: This needs to change to show more verbose errors.
    static void showErrorFrame() {
        JFrame errorFrame = new JFrame("Error!");
        errorFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(listener -> {
            errorFrame.dispose();
        });
        errorFrame.add(okButton);
        errorFrame.setSize(200, 200);
        errorFrame.setVisible(true);
    }

    // TODO: This needs to change to show more verbose errors.
    static void showSuccessFrame() {
        JFrame successFrame = new JFrame("Success!");
        successFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(listener -> {
            successFrame.dispose();
        });
        successFrame.add(okButton);
        successFrame.setSize(200, 200);
        successFrame.setVisible(true);
    }
}
