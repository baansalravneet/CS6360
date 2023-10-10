package com.librarysystem.gui;

import com.librarysystem.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class MainWindow extends JFrame { // JFrame is the main window of the application

    @Autowired
    private DatabaseService databaseService;

    public MainWindow() {
        configure();
    }

    private void configure() {
        this.setTitle("Library System");
        this.setSize(500, 500);
        this.setResizable(false);
        centerFrameOnScreen(this);

        Container content = this.getContentPane();
        content.setLayout(null);

        addSearchComponents(content);
        addCheckoutComponents(content);
        addCheckinComponents(content);
        addBorrowerComponents(content);

        this.setVisible(true);
    }

    private void addSearchComponents(Container content) {
        JLabel searchLabel = new JLabel("Search Books");
        searchLabel.setBounds(0, 30, 500, 20);
        searchLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(searchLabel);

        JTextField searchTextInput = new JTextField();
        searchTextInput.setBounds(150, 60, 200, 20);
        searchTextInput.setHorizontalAlignment(JTextField.CENTER);
        content.add(searchTextInput);

        JButton searchBookButton = new JButton("Book Search");
        searchBookButton.setBounds(200, 90, 100, 20);
        searchBookButton.addActionListener(listener -> {
            showSearchResultsFrame(searchTextInput.getText());
        });
        content.add(searchBookButton);
    }

    private void addCheckoutComponents(Container content) {
        JLabel checkoutLabel = new JLabel("Checkout Book");
        checkoutLabel.setBounds(0, 140, 500, 20);
        checkoutLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(checkoutLabel);

        JLabel isbnLabel = new JLabel("Book ISBN");
        isbnLabel.setBounds(100, 170, 100, 20);
        content.add(isbnLabel);

        JTextField checkoutTextInput = new JTextField();
        checkoutTextInput.setBounds(200, 170, 200, 20);
        content.add(checkoutTextInput);

        JLabel borrowerIdLabel = new JLabel("Card ID");
        borrowerIdLabel.setBounds(100, 200, 100, 20);
        content.add(borrowerIdLabel);

        JTextField checkoutBorrowerTextInput = new JTextField();
        checkoutBorrowerTextInput.setBounds(200, 200, 200, 20);
        content.add(checkoutBorrowerTextInput);

        JButton checkoutBookButton = new JButton("Checkout");
        checkoutBookButton.setBounds(200, 230, 100, 20);
        checkoutBookButton.addActionListener(listener -> {
            String isbn = checkoutTextInput.getText();
            String borrowerId = checkoutBorrowerTextInput.getText();
            boolean checkedOut = databaseService.checkout(List.of(isbn), borrowerId);
            if (checkedOut) showSuccessFrame();
            else showErrorFrame();
        });
        content.add(checkoutBookButton);
    }

    private void addCheckinComponents(Container content) {
        JLabel checkinLabel = new JLabel("Checkin Book");
        checkinLabel.setBounds(0, 280, 500, 20);
        checkinLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(checkinLabel);

        JTextField checkinTextInput = new JTextField();
        checkinTextInput.setBounds(150, 310, 200, 20);
        checkinTextInput.setHorizontalAlignment(JLabel.CENTER);
        content.add(checkinTextInput);

        JButton checkinBookSearchButton = new JButton("Loan Search");
        checkinBookSearchButton.setBounds(200, 340, 100, 20);
        checkinBookSearchButton.addActionListener(listener -> {
            showCheckinSearchResultsFrame(checkinTextInput.getText());
        });
        content.add(checkinBookSearchButton);
    }

    private void addBorrowerComponents(Container content) {
        JLabel borrowerLabel = new JLabel("Borrower Registration");
        borrowerLabel.setBounds(0, 390, 500, 20);
        borrowerLabel.setHorizontalAlignment(JLabel.CENTER);
        content.add(borrowerLabel);

        JButton formButton = new JButton("Open Form");
        formButton.setBounds(200, 420, 100, 20);
        formButton.addActionListener(listener -> {
            showAddBorrowerForm();
        });
        content.add(formButton);
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
        MainWindow.centerFrameOnScreen(errorFrame);
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
        MainWindow.centerFrameOnScreen(successFrame);
        successFrame.setVisible(true);
    }

    static void centerFrameOnScreen(Frame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 2;

        frame.setLocation(x, y);
    }
}
