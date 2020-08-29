package fr.musicplayer.celeria;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,"");
        }
    }
}
