package fr.musicplayer.celeria.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public Logger() {
    }

    private File homeDirectory = new File(System.getProperty("user.home") +"/.Celeria/Logs/");
    private String getStructure(){
        String datePattern = "[dd/MM/YYYY HH:mm:ss]";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        String date = dateFormat.format(new Date(System.currentTimeMillis()));
        return "[Celeria] " +  date;
    }
    private void writeLog(String data){
        if (!homeDirectory.exists()){
            if (homeDirectory.mkdirs()){
                log("Log file successfully created !");
            }else{
                error("Error when create the log file");
            }
        }
        try{
            PrintWriter writer = new PrintWriter(homeDirectory + "/Logs.log");
            writer.println(getStructure() + data);
            writer.close();
        } catch (FileNotFoundException e) {
            error(String.valueOf(e));
        }

    }
    public void log(String log){
        System.out.println(getStructure() + " [Log] " + log);
        writeLog(" [Log] " + log);

    }
    public void warn(String warn){
        System.out.println(getStructure() + " [Warn] " + warn);
        writeLog(" [Warn] " + warn);

    }
    public void error(String error){
        System.out.println(getStructure() + " [Critical Error] " + error);
        writeLog(" [Critical Error] " + error);
    }

}
