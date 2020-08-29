package fr.musicplayer.celeria.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public Logger() {
    }

    private String getStructure(){
        String datePattern = "[dd/MM/YYYY HH:mm:ss]";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        String date = dateFormat.format(new Date(System.currentTimeMillis()));
        return "[Celeria] " +  date;
    }
    public void log(String log){
        System.out.println(getStructure() + " [Log] " + log);
    }
    public void warn(String warn){
        System.out.println(getStructure() + " [Warn] " + warn);
    }
    public void error(String error){
        System.out.println(getStructure() + " [Critical Error] " + error);
    }

}
