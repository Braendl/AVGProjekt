package Logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    
    public static Date date = new Date();
    
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
    
    public static void log(String content) {
        
        String filePath = "log_" + dateFormat.format(date) + ".txt";
        System.out.println();
        try {
       
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

            writer.write(content);
            writer.newLine();
            
            writer.close();

            System.out.println("Logging erfolgreich in:" + filePath);
        } catch (IOException e) {
            System.err.println("Fehler beim Logging " + e.getMessage());
        }
    }

}
