package com.librarysystem.gui;

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

@Component
public class GUI extends JFrame { // JFrame is the main window of the application

    @Autowired
    private DatabaseService databaseService;

    private final JPanel searchPanel = new JPanel(); // we define a panel to organise components
    private final JPanel checkoutPanel = new JPanel();

    private final JTextField searchTextInput = new PromptTextField("Search Prompt");
    private final JButton searchBookButton = new JButton("Search");

    private final JTextField checkoutTextInput = new PromptTextField("Book ISBN");
    private final JTextField checkoutBorrowerTextInput = new PromptTextField("Borrower ID");
    private final JButton checkoutBookButton = new JButton("Checkout");

    private JFrame searchResultFrame;
    private JTable searchResultTable;

    private final JButton searchBookResultFrameExitButton = new JButton("OK");

    private final String[] searchResultsColumnNames = {"ISBN", "Title", "Authors", "Available"};

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
    }

    private void addComponentsToFrame() {
        searchPanel.add(searchTextInput);
        searchPanel.add(searchBookButton);
        checkoutPanel.add(checkoutTextInput);
        checkoutPanel.add(checkoutBorrowerTextInput);
        checkoutPanel.add(checkoutBookButton);
        this.add(searchPanel);
        this.add(checkoutPanel);
        pack();
    }

    private void addListeners() {
        searchBookButton.addActionListener(listener -> {
            showSearchResultsFrame(searchTextInput.getText());
        });
        searchBookResultFrameExitButton.addActionListener(listener -> {
            closeSearchResultsFrame();
        });
        checkoutBookButton.addActionListener(listener -> {
            String isbn = checkoutTextInput.getText();
            String borrowerId = checkoutBorrowerTextInput.getText();
            boolean checkedOut = databaseService.checkout(List.of(isbn), borrowerId);
            if (checkedOut) showSuccessFrame();
            else showErrorFrame();
        });
    }

    private void closeSearchResultsFrame() {
        searchResultFrame.dispose();
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
            searchResults.addAll(databaseService.getBooksForSearchQuery(s.trim().toLowerCase()).stream().distinct().toList());
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
