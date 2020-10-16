package fr.musicplayer.celeria;


import fr.musicplayer.celeria.utils.Logger;
import fr.musicplayer.celeria.utils.Utils;
import javafx.application.Application;

import javax.rmi.CORBA.Util;
import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static Logger logger = new Logger();

    public static void main(String[] args) {
        try {
            PrintWriter w = new PrintWriter(Utils.returnPathForAllSystem(".Celeria/Logs/Logs.log"));
            w.print("");
            w.close();
        } catch (FileNotFoundException e) {}

        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            logger.error("JavaFx not installed on your computer.");
            JOptionPane.showMessageDialog(null,"You must have Javafx installed on your computer", "Error" , 0);
        }
        Application.launch(FxApplication.class, args);
    }
}
