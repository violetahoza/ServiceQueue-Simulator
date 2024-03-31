package org.example.Application;

import org.example.GUI.SetupView;
import org.example.Model.LogEvents;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            LogEvents.fileInit.createNewFile(); //create a new log file
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame frame = new SetupView("Queues management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}