package com.librarysystem.gui;

import com.librarysystem.db.dao.StoredLoan;
import com.librarysystem.models.Book;
import com.librarysystem.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Try to make all of the variable local.
@Component
public class GUI extends JFrame { // JFrame is the main window of the application

    @Autowired
    private DatabaseService databaseService;

    private final JPanel searchPanel = new JPanel(); // we define a panel to organise components
    private final JPanel checkoutPanel = new JPanel();
    private final JPanel checkinPanel = new JPanel();

    private final JTextField searchTextInput = new PromptTextField("Search Prompt");
    private final JButton searchBookButton = new JButton("Search");

    private final JTextField checkoutTextInput = new PromptTextField("Book ISBN");
    private final JTextField checkoutBorrowerTextInput = new PromptTextField("Borrower ID");
    private final JButton checkoutBookButton = new JButton("Checkout");

    private final JTextField checkinTextInput = new PromptTextField("Checkin Search");
    private final JButton checkinBookSearchButton = new JButton("Search");

    private JFrame searchResultFrame;
    private JTable searchResultTable;

    private JFrame checkinSearchResultFrame;
    private JTable checkinSearchResultTable;

    private final JButton searchBookResultFrameExitButton = new JButton("OK");
    private final JButton checkinBookResultFrameExitButton = new JButton("OK");

    private final String[] searchResultsColumnNames = {"ISBN", "Title", "Authors", "Available"};
    private final String[] checkinResultColumnNames = {"ISBN", "Title", "Borrower ID", "Name", "Checkout Date", "Due Date" };

    public GUI() {
        super(); // make a new JFrame
        initialiseGUI(); // initialise with basic settings
        addComponentsToFrame(); // add all the buttons and stuff
        setSize(750, 750);
        addListeners(); // add all the functions to the buttons
    }

    private void initialiseGUI() {
        this.setTitle("Library System");
        this.setLayout(new FlowLayout());
        this.setVisible(true);

        searchPanel.setLayout(new FlowLayout());
        checkoutPanel.setLayout(new FlowLayout());
        checkinPanel.setLayout(new FlowLayout());
    }

    private void addComponentsToFrame() {
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
        pack();
    }

    private void addListeners() {
        searchBookButton.addActionListener(listener -> {
            showSearchResultsFrame(searchTextInput.getText());
        });
        searchBookResultFrameExitButton.addActionListener(listener -> {
            searchResultFrame.dispose();
        });
        checkinBookResultFrameExitButton.addActionListener(listener -> {
            checkinSearchResultFrame.dispose();
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
    }

    private void showCheckinSearchResultsFrame(String searchQuery) {
        checkinSearchResultFrame = new JFrame("Checkin Search Results");
        checkinSearchResultFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        checkinSearchResultFrame.setLayout(new FlowLayout());

        JScrollPane searchResultPane = new JScrollPane();
        addCheckinSearchResults(searchQuery);
        searchResultPane.setViewportView(checkinSearchResultTable);
        JButton checkinButton = new JButton("Checkin");
        checkinButton.addActionListener(listener -> {
            // TODO: implement this
        });
        checkinSearchResultFrame.add(searchResultPane);
        checkinSearchResultFrame.add(checkinBookResultFrameExitButton);
        checkinSearchResultFrame.add(checkinButton);
        checkinSearchResultFrame.pack();
        checkinSearchResultFrame.setVisible(true);
    }

    private void addCheckinSearchResults(String searchQuery) {
        final List<StoredLoan> searchResults = new ArrayList<>();
        for (String s : searchQuery.split(" ")) {
            if (s.isEmpty()) continue;
            searchResults.addAll(
                    databaseService.getBookLoansForSearchQuery(s.trim().toLowerCase())
                            .stream().distinct().toList()
            );
        }

        String[][] tableData = new String[searchResults.size()][6];
        for (int i = 0; i < searchResults.size(); i++) {
            tableData[i] = searchResults.get(i).displayString();
        }

        checkinSearchResultTable = new JTable(tableData, searchResultsColumnNames);
        TableModel jTableModel = new AbstractTableModel() { // this is define only to make the cells uneditable.
            @Override
            public int getRowCount() {
                return tableData.length;
            }

            @Override
            public int getColumnCount() {
                return checkinResultColumnNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return tableData[rowIndex][columnIndex];
            }

            @Override
            public String getColumnName(int columnIndex) {
                return checkinResultColumnNames[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) { // this is redundant
                return false;
            }
        };
        checkinSearchResultTable.setModel(jTableModel);
        checkinSearchResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        checkinSearchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checkinSearchResultTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    String isbn = tableData[row][0];
                    StoredLoan loan = searchResults.stream().filter(b -> b.getBook().getIsbn().equals(isbn)).findFirst().get();
                    showLoanInfoFrame(loan);
                }
            }
        });
    }

    private void showSearchResultsFrame(String searchQuery) {
        searchResultFrame = new JFrame("Search Results");
        searchResultFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        searchResultFrame.setLayout(new FlowLayout());

        JScrollPane searchResultPane = new JScrollPane();
        addSearchResults(searchQuery);
        searchResultPane.setViewportView(searchResultTable);
        searchResultFrame.add(searchResultPane);
        searchResultFrame.add(searchBookResultFrameExitButton);
        JButton checkoutButton = new JButton("Checkout");
        JTextField checkoutBorrowerSearchTextInput = new PromptTextField("Borrower ID");
        checkoutButton.addActionListener(listener -> {
            List<String> selectedISBN = Arrays.stream(searchResultTable.getSelectedRows())
                    .mapToObj(row -> (String) searchResultTable.getValueAt(row, 0)).toList();
            String borrowerId = checkoutBorrowerSearchTextInput.getText();
            boolean checkedOut = databaseService.checkout(selectedISBN, borrowerId);
            if (!checkedOut) showErrorFrame();
            else showSuccessFrame();
        });
        searchResultFrame.add(checkoutBorrowerSearchTextInput);
        searchResultFrame.add(checkoutButton);
        searchResultFrame.pack();
        searchResultFrame.setVisible(true);
    }

    // TODO: add feature to search the intersection. Eg, all books named "GOOD BOOK" by "JOHN"
    // TODO: fix table size
    private void addSearchResults(String searchQuery) {
        final List<Book> searchResults = new ArrayList<>();
        for (String s : searchQuery.split(" ")) {
            if (s.isEmpty()) continue;
            searchResults.addAll(
                    databaseService.getBooksForSearchQuery(s.trim().toLowerCase())
                            .stream().distinct().toList()
            );
        }

        String[][] tableData = new String[searchResults.size()][4];
        for (int i = 0; i < searchResults.size(); i++) {
            tableData[i] = searchResults.get(i).displayString();
        }

        searchResultTable = new JTable(tableData, searchResultsColumnNames);
        TableModel jTableModel = new AbstractTableModel() { // this is define only to make the cells uneditable.
            @Override
            public int getRowCount() {
                return tableData.length;
            }

            @Override
            public int getColumnCount() {
                return searchResultsColumnNames.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return tableData[rowIndex][columnIndex];
            }

            @Override
            public String getColumnName(int columnIndex) {
                return searchResultsColumnNames[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) { // this is redundant
                return false;
            }
        };
        searchResultTable.setModel(jTableModel);
        searchResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        searchResultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        searchResultTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    String isbn = tableData[row][0];
                    Book book = searchResults.stream().filter(b -> b.getIsbn().equals(isbn)).findFirst().get();
                    showBookInfoFrame(book);
                }
            }
        });
    }

    private void showLoanInfoFrame(StoredLoan loan) {
        JFrame loanInfoFrame = new JFrame("Loan Info");
        loanInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanInfoFrame.setLayout(new FlowLayout());

        loanInfoFrame.add(new TextArea(loan.getLoanInfoString()));

        loanInfoFrame.pack();
        loanInfoFrame.setVisible(true);
    }

    private void showBookInfoFrame(Book book) {
        JFrame bookInfoFrame = new JFrame("Book Info");
        bookInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookInfoFrame.setLayout(new FlowLayout());

        bookInfoFrame.add(new TextArea(book.getBookInfoString()));

        bookInfoFrame.pack();
        bookInfoFrame.setVisible(true);
    }

    private void showErrorFrame() {
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

    private void showSuccessFrame() {
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
