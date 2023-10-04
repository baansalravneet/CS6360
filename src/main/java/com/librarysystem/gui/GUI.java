package com.librarysystem.gui;

import com.librarysystem.models.Book;
import com.librarysystem.services.BookService;
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

// TODO: Add feature to checkout using ISBN

@Component
public class GUI extends JFrame { // JFrame is the main window of the application

    @Autowired
    private BookService bookService;

    private final JPanel searchPanel = new JPanel(); // we define a panel to organise components

    private final JButton searchBookButton = new JButton("Search");
    private final JTextField textInput = new JTextField(25);

    private JFrame searchResultFrame;
    private JTable searchResultTable;

    private final JButton searchBookResultFrameExitButton = new JButton("OK");

    private final String[] searchResultsColumnNames = {"ISBN", "Title", "Authors", "Available"};

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
        searchResultFrame.setLayout(new FlowLayout());

        JScrollPane searchResultPane = new JScrollPane();
        addSearchResults(searchQuery);
        searchResultPane.setViewportView(searchResultTable);
        searchResultFrame.add(searchResultPane);
        searchResultFrame.add(searchBookResultFrameExitButton);
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(listener -> {
            List<String> selectedISBN = Arrays.stream(searchResultTable.getSelectedRows())
                    .filter(row -> searchResultTable.getValueAt(row, 3).equals("Yes"))
                    .mapToObj(row -> (String)searchResultTable.getValueAt(row, 0)).toList();
            for (String s : selectedISBN) {
                System.out.println(s);
            }
        });
        searchResultFrame.add(checkoutButton);
        searchResultFrame.pack();
        searchResultFrame.setVisible(true);
    }

    // TODO: add feature to search the intersection. Eg, all books named "GOOD BOOK" by "JOHN"
    // TODO: fix table size
    private void addSearchResults(String searchQuery) {
        final List<Book> searchResults = new ArrayList<>();
        for (String s : searchQuery.split(" ")) {
            searchResults.addAll(bookService.getBooksForSearchQuery(s.trim().toLowerCase()).stream().distinct().toList());
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
                JTable table = (JTable)mouseEvent.getSource();
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
        bookInfoFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        bookInfoFrame.setLayout(new FlowLayout());

        bookInfoFrame.add(new TextArea(book.getBookInfoString()));

        bookInfoFrame.pack();
        bookInfoFrame.setVisible(true);
    }
}
