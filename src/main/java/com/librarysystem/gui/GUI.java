package com.librarysystem.gui;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class GUI extends JFrame { // JFrame is the main window of the application

    private final JPanel searchPanel = new JPanel(); // we define a panel to organise components

    private final JButton searchBookButton = new JButton("Search");
    private final JTextField textInput = new JTextField(25);

    private JFrame searchResultFrame;
//    private final JFrame searchBookResultFrame = new JFrame();
    private final JButton searchBookResultFrameExitButton = new JButton("OK");

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
            showSearchResultsFrame();
        });
        searchBookResultFrameExitButton.addActionListener(listener -> {
            closeSearchResultsFrame();
        });
    }

    private void closeSearchResultsFrame() {
        searchResultFrame.dispose();
    }

    private void showSearchResultsFrame() {
        searchResultFrame = new JFrame("Search Results");
        searchResultFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        searchResultFrame.setSize(250, 250);
        searchResultFrame.setLayout(new FlowLayout());
        searchResultFrame.add(searchBookResultFrameExitButton);
        searchResultFrame.setVisible(true);
    }
}
