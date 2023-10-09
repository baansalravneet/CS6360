package com.librarysystem.gui;

import com.librarysystem.gui.customcomponents.PromptTextField;
import com.librarysystem.models.Book;
import com.librarysystem.services.DatabaseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookSearchWindow extends JFrame {

    private static final String[] searchResultsColumnNames = {"ISBN", "Title", "Authors", "Available"};

    private DatabaseService databaseService;

    public BookSearchWindow(String searchQuery, DatabaseService databaseService) {
        super("Book Search Results");
        this.databaseService = databaseService;
        configure();
        initialiseWithData(searchQuery);
    }

    private void configure() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout());
    }

    private void initialiseWithData(String searchQuery) {
        JScrollPane searchResultPane = new JScrollPane();
        JTable searchResultTable = addSearchResults(searchQuery);
        searchResultPane.setViewportView(searchResultTable);
        this.add(searchResultPane);
        JButton searchBookResultFrameExitButton = new JButton("OK");
        searchBookResultFrameExitButton.addActionListener(listener -> {
            this.dispose();
        });
        this.add(searchBookResultFrameExitButton);
        JButton checkoutButton = new JButton("Checkout");
        JTextField checkoutBorrowerSearchTextInput = new PromptTextField("Borrower ID");
        checkoutButton.addActionListener(listener -> {
            List<String> selectedISBN = Arrays.stream(searchResultTable.getSelectedRows())
                    .mapToObj(row -> (String) searchResultTable.getValueAt(row, 0)).toList();
            String borrowerId = checkoutBorrowerSearchTextInput.getText();
            boolean checkedOut = databaseService.checkout(selectedISBN, borrowerId);
            if (!checkedOut) MainWindow.showErrorFrame();
            else MainWindow.showSuccessFrame();
        });
        this.add(checkoutBorrowerSearchTextInput);
        this.add(checkoutButton);
        this.pack();
        this.setVisible(true);
    }

    // TODO: add feature to search the intersection. Eg, all books named "GOOD BOOK" by "JOHN"
    // TODO: fix table size
    private JTable addSearchResults(String searchQuery) {
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

        JTable searchResultTable = new JTable(tableData, searchResultsColumnNames);
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
        return searchResultTable;
    }

    private void showBookInfoFrame(Book book) {
        JFrame bookInfoFrame = new JFrame("Book Info");
        bookInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookInfoFrame.setLayout(new FlowLayout());

        bookInfoFrame.add(new TextArea(book.getBookInfoString()));

        bookInfoFrame.pack();
        bookInfoFrame.setVisible(true);
    }
}
