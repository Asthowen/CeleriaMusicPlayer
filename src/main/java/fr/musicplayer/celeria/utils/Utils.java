package fr.musicplayer.celeria.utils;

import java.io.File;

public class Utils {
    public static File returnPathForAllSystem(String path){
        File folder;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            folder = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\" + path.replace("/", "\\"));
        else if (os.contains("mac"))
            folder =  new File(System.getProperty("user.home") + "/Library/Application Support/" + path);
        else
            folder =  new File(System.getProperty("user.home") + "/" + path);
        return folder;
    }
}
