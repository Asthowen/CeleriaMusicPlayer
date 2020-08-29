package fr.musicplayer.celeria;


import fr.musicplayer.celeria.utils.Logger;
import javafx.application.Application;

import javax.swing.*;

public class Main {
    public static Logger logger = new Logger();
    public static void main(String[] args) {
        //Wav.playMusic("/mnt/sda2/Téléchargements/mp3/lmusic.wav");
        try {
            Class.forName("javafx.application.Application");
        } catch (ClassNotFoundException e) {
            logger.error("JavaFx not installed on your computer.");
            JOptionPane.showMessageDialog(null,"You must have Javafx installed on your computer", "Error" , 0);
        }

        Application.launch(FxApplication.class, args);
    }
}
