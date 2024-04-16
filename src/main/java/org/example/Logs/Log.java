package org.example.Logs;

import java.io.*;

public class Log implements LogWriter{
    @Override
    public boolean logWrite(String message) {
        try(FileWriter fileWriter = new FileWriter("log.txt", true)) {
            fileWriter.write(message);
            fileWriter.write("\n");


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
