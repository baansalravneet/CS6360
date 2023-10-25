package com.librarysystem.gui;

import com.librarysystem.db.dao.StoredLoan;
import com.librarysystem.models.Response;
import com.librarysystem.services.DatabaseService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class FineControlPanel extends JFrame {
    private static final String[] columnNames = new String[]{"Loan ID", "Amount", "Paid"};

    private DatabaseService databaseService;
    private JScrollPane tablePane;
    private JTable feeTable;
    private JLabel totalDueLabel;

    public FineControlPanel(DatabaseService databaseService) {
        this.databaseService = databaseService;
        configure();
    }

    private void configure() {
        this.setTitle("Fine Control Panel");
        this.setBounds(0, 0, 350, 400);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        MainWindow.centerFrameOnScreen(this);

        Container content = this.getContentPane();
        content.setLayout(null);

        JButton updateFines = new JButton("Update Fines");
        updateFines.setBounds(125, 30, 100, 20);
        updateFines.addActionListener(listener -> {
            if (databaseService.updateFines()) {
                MainWindow.showResponseFrame(new Response());
            } else {
                MainWindow.showResponseFrame(new Response("Error Occurred"));
            }
        });
        content.add(updateFines);

        JLabel borrowerId = new JLabel("Borrower ID");
        borrowerId.setBounds(30, 80, 100, 20);
        content.add(borrowerId);

        JTextField borrowerIdInput = new JTextField();
        borrowerIdInput.setBounds(130, 80, 190, 20);
        content.add(borrowerIdInput);

        JCheckBox filterPaidCheckBox = new JCheckBox("Filter paid loans");
        filterPaidCheckBox.setBounds(30, 110, 140,20);
        content.add(filterPaidCheckBox);

        JButton findDueButton = new JButton("Find Fee Due");
        findDueButton.setBounds(200, 110, 100, 20);
        findDueButton.addActionListener(listener -> {
            String cardId = borrowerIdInput.getText();
            showFines(cardId, content, filterPaidCheckBox.isSelected());
        });
        content.add(findDueButton);

        JButton payFeeButton = new JButton("Pay Fee");
        payFeeButton.setBounds(200, 330, 100, 20);
        payFeeButton.addActionListener(listener -> {
            if (feeTable == null) return;
            int row = feeTable.getSelectedRow();
            if (row == -1) return;
            long loanId = Long.parseLong((String) feeTable.getValueAt(row, 0));
            if (databaseService.handleFeePayment(loanId)) MainWindow.showResponseFrame(new Response());
            else MainWindow.showResponseFrame(new Response("Error Occurred"));
        });
        content.add(payFeeButton);

        totalDueLabel = new JLabel("");
        totalDueLabel.setBounds( 50, 330, 140, 20);
        content.add(totalDueLabel);

        this.setVisible(true);
    }

    private void showFines(String cardId, Container content, boolean filterPaid) {
        if (tablePane != null) this.remove(tablePane);
        feeTable = new JTable();
        populateTable(feeTable, cardId, filterPaid);
        tablePane = new JScrollPane();
        tablePane.setBounds(10, 140, 320, 180);
        tablePane.setViewportView(feeTable);
        content.add(tablePane);
    }

    private double populateTable(JTable feeTable, String cardId, boolean filterPaid) {
        List<StoredLoan> loans = databaseService.getLoansByBorrowerId(cardId);
        double totalDue = loans.stream()
                .filter(l -> l.getFine() != null)
                .filter(l -> !l.getFine().isPaid())
                .mapToDouble(l -> l.getFine().getFineAmount())
                .sum();
        totalDueLabel.setText(String.format("Total Due: %.2f", totalDue));
        List<StoredLoan> filteredLoans = loans.stream()
                .filter(l -> l.getFine() != null)
                .filter(l -> {
                    if (filterPaid) return !l.getFine().isPaid();
                    return true;
                })
                .toList();
        if (filteredLoans.isEmpty()) {
            MainWindow.showResponseFrame(new Response("No loans to show"));
            return 0;
        }
        String[][] tableData = new String[filteredLoans.size()][3];
        for (int i = 0; i < filteredLoans.size(); i++) {
            tableData[i][0] = String.valueOf(filteredLoans.get(i).getId());
            tableData[i][1] = String.format("%.2f", filteredLoans.get(i).getFine().getFineAmount());
            tableData[i][2] = filteredLoans.get(i).getFine().isPaid() ? "Yes" : "No";
        }

        TableModel jTableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return tableData.length;
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return tableData[rowIndex][columnIndex];
            }

            @Override
            public String getColumnName(int columnIndex) {
                return columnNames[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) { // this is redundant
                return false;
            }
        };
        feeTable.setModel(jTableModel);
        feeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return totalDue;
    }
}
