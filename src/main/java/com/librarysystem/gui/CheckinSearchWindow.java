package com.librarysystem.gui;

import com.librarysystem.db.dao.StoredLoan;
import com.librarysystem.models.Response;
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

    private static final String[] checkinResultColumnNames =
        {
            "ID", "ISBN", "Title", "Borrower ID", "Borrower Name", "Checkout Date", "Due Date", "Checkin Date"
        };

    private DatabaseService databaseService;

    public CheckinSearchWindow(String searchQuery, DatabaseService databaseService) {
        this.databaseService = databaseService;
        initialiseWithData(searchQuery);
    }

    private void initialiseWithData(String searchQuery) {
        this.setTitle("Checkin Search Results");
        this.setSize(750, 750);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        MainWindow.centerFrameOnScreen(this);

        Container content = this.getContentPane();
        content.setLayout(null);

        JTable table = addTable(searchQuery, content);
        addCheckinComponents(table, content);

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
        table.setModel(jTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    long loanId = Long.parseLong(tableData[row][0]);
                    StoredLoan loan = searchResults.stream()
                            .filter(l -> l.getId().equals(loanId)).findFirst().get();
                    showLoanInfoFrame(loan);
                }
            }
        });
    }

    private void addCheckinComponents(JTable table, Container content) {
        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(600, 665, 100, 20);
        exitButton.addActionListener(listener -> {
            this.dispose();
        });
        content.add(exitButton);

        JButton checkinButton = new JButton("Checkin");
        checkinButton.setBounds(30, 665, 100, 20);
        checkinButton.addActionListener(listener -> {
            int[] selectedRow = table.getSelectedRows();
            if (selectedRow.length == 0) {
                MainWindow.showResponseFrame(new Response("Please select a row"));
                return;
            }
            long loanId = Long.parseLong((String) table.getValueAt(selectedRow[0], 0));
            Response checkedIn = databaseService.checkin(loanId);
            MainWindow.showResponseFrame(checkedIn);
        });
        content.add(checkinButton);
    }

    private void showLoanInfoFrame(StoredLoan loan) {
        JFrame loanInfoFrame = new JFrame("Loan Info");
        loanInfoFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loanInfoFrame.setLayout(new FlowLayout());

        loanInfoFrame.add(new TextArea(loan.getLoanInfoString()));

        loanInfoFrame.pack();
        MainWindow.centerFrameOnScreen(loanInfoFrame);
        loanInfoFrame.setVisible(true);
    }


}
