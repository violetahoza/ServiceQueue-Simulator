package org.example.Model;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LogEvents {
    public static final File fileInit = new File("log.txt"); //define  a file to store logs
    public static FileWriter file = null; // FileWriter to write logs to the file
    public static AtomicInteger simulationTime = new AtomicInteger(0); // AtomicIntegers to store simulation parameters and states
    public static AtomicInteger clientsNr = new AtomicInteger(0);
    public static AtomicInteger queuesNr = new AtomicInteger(0);
    public static AtomicInteger minArrival = new AtomicInteger(0);
    public static AtomicInteger maxArrival = new AtomicInteger(0);
    public static AtomicInteger minService = new AtomicInteger(0);
    public static AtomicInteger maxService = new AtomicInteger(0);

    // static block to initialize the FileWriter and create the log file, because I want the initialization to happen only one time + thread safety
    static {
        try {
            file = new FileWriter("log.txt");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public static synchronized void log(String message) {
        try {
            file.write(message + "\n"); // write the message to the log file
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void close() {
        try {
            file.close(); // close the FileWriter
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
