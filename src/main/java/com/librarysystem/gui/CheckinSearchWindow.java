package com.librarysystem.gui;

import com.librarysystem.db.dao.StoredLoan;
import com.librarysystem.services.DatabaseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CheckinSearchWindow extends JFrame {

    private static final String[] checkinResultColumnNames = {"ISBN", "Title", "Borrower ID", "Name", "Checkout Date", "Due Date", "Checkin Date"};

    private DatabaseService databaseService;

    public CheckinSearchWindow(String searchQuery, DatabaseService databaseService) {
        super("Checkin Search Results");
        this.databaseService = databaseService;
        configure();
        initialiseWithData(searchQuery);
    }

    private void configure() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout());
    }

    private void initialiseWithData(String searchQuery) {
        JScrollPane searchResultPane = new JScrollPane();
        JTable checkinSearchResultTable = addCheckinSearchResults(searchQuery);
        searchResultPane.setViewportView(checkinSearchResultTable);
        JButton checkinButton = new JButton("Checkin");
        checkinButton.addActionListener(listener -> {
            int[] selectedRow = checkinSearchResultTable.getSelectedRows();
            if (selectedRow.length == 0) return;
            String isbn = (String) checkinSearchResultTable.getValueAt(selectedRow[0], 0);
            String borrowerId = (String) checkinSearchResultTable.getValueAt(selectedRow[0], 2);
            boolean checkin = databaseService.checkin(isbn, borrowerId);
            if (!checkin) MainWindow.showErrorFrame();
            else MainWindow.showSuccessFrame();
        });
        JButton checkinBookResultFrameExitButton = new JButton("OK");
        checkinBookResultFrameExitButton.addActionListener(listener -> {
            this.dispose();
        });
        this.add(searchResultPane);
        this.add(checkinBookResultFrameExitButton);
        this.add(checkinButton);
        this.pack();
        this.setVisible(true);
    }

    private JTable addCheckinSearchResults(String searchQuery) {
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

        JTable checkinSearchResultTable = new JTable(tableData, checkinResultColumnNames);
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
                    StoredLoan loan = searchResults.stream()
                            .filter(b -> b.getBook().getIsbn().equals(isbn)).findFirst().get();
                    showLoanInfoFrame(loan);
                }
            }
        });
        return checkinSearchResultTable;
    }

    private void showLoanInfoFrame(StoredLoan loan) {
        JFrame loanInfoFrame = new JFrame("Loan Info");
        loanInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanInfoFrame.setLayout(new FlowLayout());

        loanInfoFrame.add(new TextArea(loan.getLoanInfoString()));

        loanInfoFrame.pack();
        loanInfoFrame.setVisible(true);
    }


}
