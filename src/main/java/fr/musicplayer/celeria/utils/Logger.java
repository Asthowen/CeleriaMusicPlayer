package fr.musicplayer.celeria.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public Logger() {
    }

    public File homeDirectory = new File(System.getProperty("user.home") +"/.Celeria/Logs/");

    public File getHomeDirectory() {
        return homeDirectory;
    }

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
            FileWriter fw = new FileWriter(homeDirectory + "/Logs.log",true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(getStructure() + data);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
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
