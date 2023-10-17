package com.librarysystem.gui;

import com.librarysystem.db.dao.StoredLoan;
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
                MainWindow.showSuccessFrame();
            } else {
                MainWindow.showErrorFrame();
            }
        });
        content.add(updateFines);

        JLabel borrowerId = new JLabel("Borrower ID");
        borrowerId.setBounds(30, 80, 100, 20);
        content.add(borrowerId);

        JTextField borrowerIdInput = new JTextField();
        borrowerIdInput.setBounds(130, 80, 190, 20);
        content.add(borrowerIdInput);

        JButton findDueButton = new JButton("Find Fee Due");
        findDueButton.setBounds(125, 110, 100, 20);
        findDueButton.addActionListener(listener -> {
            String cardId = borrowerIdInput.getText();
            showFines(cardId, content);
        });
        content.add(findDueButton);

        JButton payFeeButton = new JButton("Pay Fee");
        payFeeButton.setBounds(125, 330, 100, 20);
        payFeeButton.addActionListener(listener -> {
            if (feeTable == null) return;
            int row = feeTable.getSelectedRow();
            if (row == -1) return;
            long loanId = Long.parseLong((String) feeTable.getValueAt(row, 0));
            if (databaseService.handleFeePayment(loanId)) MainWindow.showSuccessFrame();
            else MainWindow.showErrorFrame();
        });
        content.add(payFeeButton);

        this.setVisible(true);
    }

    private void showFines(String cardId, Container content) {
        if (tablePane != null) this.remove(tablePane);
        feeTable = new JTable();
        populateTable(feeTable, cardId);
        tablePane = new JScrollPane();
        tablePane.setBounds(10, 140, 320, 180);
        tablePane.setViewportView(feeTable);
        content.add(tablePane);


    }

    private void populateTable(JTable feeTable, String cardId) {
        List<StoredLoan> loans = databaseService.getLoansByBorrowerId(cardId)
                .stream()
                .filter(l -> l.getFine() != null)
                .toList();
        if (loans.isEmpty()) {
            MainWindow.showErrorFrame();
            return;
        }
        String[][] tableData = new String[loans.size()][3];
        for (int i = 0; i < loans.size(); i++) {
            tableData[i][0] = String.valueOf(loans.get(i).getId());
            tableData[i][1] = String.valueOf(loans.get(i).getFine().getFineAmount());
            tableData[i][2] = loans.get(i).getFine().isPaid() ? "Yes" : "No";
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
    }
}
