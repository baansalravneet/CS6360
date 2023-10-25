package com.librarysystem.gui;

import com.librarysystem.models.Book;
import com.librarysystem.models.Response;
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

// TODO: add feature to search the intersection. Eg, all books named "GOOD BOOK" by "JOHN"
public class BookSearchWindow extends JFrame {

    private static final String[] searchResultsColumnNames = {"ISBN", "Title", "Authors", "Available"};

    private DatabaseService databaseService;

    public BookSearchWindow(String searchQuery, DatabaseService databaseService) {
        this.databaseService = databaseService;
        initialiseWithData(searchQuery);
    }

    private void initialiseWithData(String seachQuery) {
        this.setTitle("Book Search Results");
        this.setSize(750, 750);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        MainWindow.centerFrameOnScreen(this);

        Container content = this.getContentPane();
        content.setLayout(null);

        JTable table = addTable(seachQuery, content);
        addCheckoutComponents(table, content);

        this.setVisible(true);
    }

    private JTable addTable(String searchQuery, Container content) {
        JTable searchResultTable = new JTable();

        populateTable(searchResultTable, searchQuery);

        JScrollPane searchResultPane = new JScrollPane();
        searchResultPane.setBounds(30, 30, 690, 600);
        searchResultPane.setViewportView(searchResultTable);
        content.add(searchResultPane);
        return searchResultTable;
    }

    private void populateTable(JTable table, String searchQuery) {
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

        TableModel jTableModel = new AbstractTableModel() {
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
        table.setModel(jTableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.addMouseListener(new MouseAdapter() {
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

    private void addCheckoutComponents(JTable table, Container content) {
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(600, 665, 100, 20);
        exitButton.addActionListener(listener -> {
            this.dispose();
        });
        content.add(exitButton);

        JLabel borrower = new JLabel("Card ID");
        borrower.setBounds(50, 665, 70, 20);
        content.add(borrower);

        JTextField cardId = new JTextField();
        cardId.setBounds(120, 665, 200, 20);
        content.add(cardId);

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setBounds(350, 665, 100, 20);
        content.add(checkoutButton);

        checkoutButton.addActionListener(listener -> {
            List<String> selectedISBN = Arrays.stream(table.getSelectedRows())
                    .mapToObj(row -> (String) table.getValueAt(row, 0)).toList();
            String borrowerId = cardId.getText();
            boolean checkedOut = databaseService.checkout(selectedISBN, borrowerId);
            if (!checkedOut) MainWindow.showResponseFrame(new Response("Error Occurred"));
            else MainWindow.showResponseFrame(new Response());
        });
    }

    private void showBookInfoFrame(Book book) {
        JFrame bookInfoFrame = new JFrame("Book Info");
        bookInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookInfoFrame.setLayout(new FlowLayout());

        bookInfoFrame.add(new TextArea(book.getBookInfoString()));

        bookInfoFrame.pack();
        MainWindow.centerFrameOnScreen(bookInfoFrame);
        bookInfoFrame.setVisible(true);
    }
}
