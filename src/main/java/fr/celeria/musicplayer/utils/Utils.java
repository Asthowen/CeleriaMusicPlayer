package fr.celeria.musicplayer.utils;

import fr.flowarg.flowcompat.Platform;

import java.io.File;

public class Utils {
    public static File returnHomeForOS(String path) {
        File folder;
        if (Platform.isOnWindows()) folder = new File(System.getenv("APPDATA"), path.replace("/", File.separator));
        else if (Platform.isOnMac()) folder =  new File(System.getProperty("user.home") + "/Library/Application Support/" + path);
        else folder =  new File(System.getProperty("user.home") + File.separator + path);

        return folder;
    }
}
