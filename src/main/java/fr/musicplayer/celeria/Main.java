package fr.musicplayer.celeria;

import javafx.application.Application;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"You must have Javafx installed on your computer", "Error" , 0);
        }

        Application.launch(FxApplication.class, args);
    }
}
