package fr.celeria.musicplayer;


import fr.celeria.musicplayer.utils.Logger;
import fr.celeria.musicplayer.utils.Utils;
import javafx.application.Application;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {
    public static Logger logger = new Logger();

    public static void main(String[] args) {
        try {
            PrintWriter w = new PrintWriter(Utils.returnHomeForOS(".Celeria/Logs/Logs.log"));
            w.print("");
            w.close();
        } catch (FileNotFoundException e) {logger.error(e.getMessage());}

        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            logger.error("JavaFx not installed on your computer.");
            JOptionPane.showMessageDialog(null,"You must have Javafx installed on your computer", "Error" , 0);
        }
        Application.launch(FxApplication.class, args);
    }
}
