package com.librarysystem.gui;

import com.librarysystem.services.DatabaseService;

import javax.swing.*;
import java.awt.*;

public class FineControlPanel extends JFrame {
    private DatabaseService databaseService;

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



        this.setVisible(true);
    }
}
