package org.example.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LogEvents {
    public static final File fileInit = new File("log.txt");
    public static FileWriter file = null;
    public static AtomicInteger simulationTime = new AtomicInteger(0);
    public static AtomicInteger clientsNr = new AtomicInteger(0);
    public static AtomicInteger queuesNr = new AtomicInteger(0);
    public static AtomicInteger minArrival = new AtomicInteger(0);
    public static AtomicInteger maxArrival = new AtomicInteger(0);
    public static AtomicInteger minService = new AtomicInteger(0);
    public static AtomicInteger maxService = new AtomicInteger(0);


    static {
        try {
            file = new FileWriter("log.txt");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static synchronized void log(String message) {
        try {
            file.write(message + "\n");
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void close() {
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
