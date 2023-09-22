package com.librarysystem.gui;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class GUI extends JFrame {

    JButton printThisButton = new JButton("Print This");
    JTextField textInput = new JTextField(20);

    public GUI() {
        super("Library System");
        initGUI();
        addListeners();
    }

    private void initGUI() {
        addComponentsToPane(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }

    private void addComponentsToPane(final Container pane) {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(textInput);
        panel.add(printThisButton);
        pane.add(panel);
    }

    private void addListeners() {
        printThisButton.addActionListener(listener -> {
            JOptionPane.showMessageDialog(null, textInput.getText());
        });
    }
}
