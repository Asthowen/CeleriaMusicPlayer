package fr.celeria.musicplayer.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public Logger() {}

    private String getStructure(){
        return "[Celeria] " + new SimpleDateFormat("[dd/MM/YYYY HH:mm:ss]").format(new Date(System.currentTimeMillis()));
    }

    private void writeLog(String data){
        if (!Utils.returnHomeForOS(".Celeria/Logs/").exists()){
            if (Utils.returnHomeForOS(".Celeria/Logs/").mkdirs()){
                log("Log file successfully created !");
            }else{
                error("Error when create the log file");
            }
        }
        try{
            FileWriter fw = new FileWriter(Utils.returnHomeForOS(".Celeria/Logs/Logs.log"),true);
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
        System.err.println(getStructure() + " [Critical Error] " + error);
        writeLog(" [Critical Error] " + error);
    }

}
