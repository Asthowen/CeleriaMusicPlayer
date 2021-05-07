package fr.celeria.musicplayer;

import fr.celeria.musicplayer.utils.Utils;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import javafx.application.Application;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {
    public static final ILogger LOGGER = new Logger("[Celeria]", Utils.returnHomeForOS(".Celeria/Logs/Logs.log"));

    public static void main(String[] args) {
        try{
            Class.forName("javafx.application.Application");
        }
        catch (ClassNotFoundException e){
            LOGGER.err("JavaFx not installed on your computer.");
            JOptionPane.showMessageDialog(null,"You must have Javafx installed on your computer", "Error" , 0);
        }

        Application.launch(FxApplication.class, args);
    }
}
