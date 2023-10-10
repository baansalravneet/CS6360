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
        setSize(411, 239);
    }

    private void configure() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setLocation(600, 400);
        this.setResizable(false);
        this.setVisible(true);

        JPanel searchPanel = new JPanel();
        JPanel checkoutPanel = new JPanel();
        JPanel checkoutFormPanel = new JPanel();
        JPanel checkinPanel = new JPanel();
        JPanel addBorrowerFormButtonPanel = new JPanel();

        searchPanel.setLayout(new FlowLayout());
        checkoutFormPanel.setLayout(new BoxLayout(checkoutFormPanel, BoxLayout.Y_AXIS));
        checkoutPanel.setLayout(new FlowLayout());
        checkinPanel.setLayout(new FlowLayout());
        addBorrowerFormButtonPanel.setLayout(new FlowLayout());

        JTextField searchTextInput = new PromptTextField("Search Prompt");
        JButton searchBookButton = new JButton("Book Search");

        JTextField checkoutTextInput = new PromptTextField("Book ISBN");
        JTextField checkoutBorrowerTextInput = new PromptTextField("Borrower ID");
        JButton checkoutBookButton = new JButton("Checkout");

        JTextField checkinTextInput = new PromptTextField("Checkin Search Prompt");
        JButton checkinBookSearchButton = new JButton("Checkin Search");

        JButton addBorrowerFormButton = new JButton("Show Add Borrower Form");

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
        addBorrowerFormButton.addActionListener(listener -> {
            showAddBorrowerForm();
        });

        searchPanel.add(searchTextInput);
        searchPanel.add(searchBookButton);

        checkoutFormPanel.add(checkoutTextInput);
        checkoutFormPanel.add(checkoutBorrowerTextInput);
        checkoutPanel.add(checkoutFormPanel);
        checkoutPanel.add(checkoutBookButton);

        checkinPanel.add(checkinTextInput);
        checkinPanel.add(checkinBookSearchButton);

        addBorrowerFormButtonPanel.add(addBorrowerFormButton);

        this.add(searchPanel);
        this.add(checkoutPanel);
        this.add(checkinPanel);
        this.add(addBorrowerFormButtonPanel);
        this.pack();
    }

    private void showCheckinSearchResultsFrame(String searchQuery) {
        new CheckinSearchWindow(searchQuery, databaseService);
    }

    private void showSearchResultsFrame(String searchQuery) {
        new BookSearchWindow(searchQuery, databaseService);
    }

    private void showAddBorrowerForm() {
        new AddBorrowerForm(databaseService);
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
