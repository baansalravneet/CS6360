package com.librarysystem.gui;

import com.librarysystem.models.Book;
import com.librarysystem.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class GUI extends JFrame { // JFrame is the main window of the application

    @Autowired
    private BookService bookService;

    private final JPanel searchPanel = new JPanel(); // we define a panel to organise components

    private final JButton searchBookButton = new JButton("Search");
    private final JTextField textInput = new JTextField(25);

    private JFrame searchResultFrame;

    private final JButton searchBookResultFrameExitButton = new JButton("OK");

    private final String[] searchResultsColumnNames = { "ISBN", "Title", "Available" };

    public GUI() {
        super(); // make a new JFrame
        initialiseGUI(); // initialise with basic settings
        addComponentsToPane(this.getContentPane()); // add all the buttons and stuff
        setSize(750, 750);
        addListeners(); // add all the functions to the buttons
    }

    private void initialiseGUI() {
        this.setTitle("Library System");
        this.setLayout(new FlowLayout());
        this.setVisible(true);
    }

    private void addComponentsToPane(final Container pane) {
        searchPanel.add(textInput);
        searchPanel.add(searchBookButton);
        pane.add(searchPanel);
        pack();
    }

    private void addListeners() {
        searchBookButton.addActionListener(listener -> {
            showSearchResultsFrame(textInput.getText());
        });
        searchBookResultFrameExitButton.addActionListener(listener -> {
            closeSearchResultsFrame();
        });
    }

    private void closeSearchResultsFrame() {
        searchResultFrame.dispose();
    }

    private void showSearchResultsFrame(String searchQuery) {
        searchResultFrame = new JFrame("Search Results");
        searchResultFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        searchResultFrame.setSize(250, 250);
        searchResultFrame.setLayout(new FlowLayout());

        JScrollPane searchResultPane = new JScrollPane(addSearchResults(searchQuery));
        searchResultFrame.add(searchResultPane);
        searchResultFrame.add(searchBookResultFrameExitButton);
        searchResultFrame.pack();
        searchResultFrame.setVisible(true);
    }

    // TODO: add feature to search the intersection. Eg, all books named "GOOD BOOK" by "JOHN"
    private JTable addSearchResults(String searchQuery) {
        List<Book> searchResults = new ArrayList<>();
        for (String s : searchQuery.split(" ")) {
            searchResults.addAll(bookService.getBooksForSearchQuery(s.trim().toLowerCase()));
        }
        searchResults = searchResults.stream().distinct().toList();

        String[][] tableData = new String[searchResults.size()][3];
        for (int i = 0; i < searchResults.size(); i++) {
            tableData[i] = searchResults.get(i).displayString();
        }

        JTable jTable = new JTable(tableData, searchResultsColumnNames);
        jTable.setEnabled(false);
        return jTable;
    }
}
