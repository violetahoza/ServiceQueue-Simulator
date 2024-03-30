package org.example.Application;

import org.example.GUI.SetupView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new SetupView("Queues management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}