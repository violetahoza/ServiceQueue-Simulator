package org.example.GUI;

import org.example.Model.Client;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientBox extends JTextArea {
    Client client;
    AtomicInteger initialServiceTime = new AtomicInteger();

    public ClientBox(Client client) {
        this.setFont(new Font("Arial", Font.BOLD, 15));
        this.setForeground(new Color(128, 0, 128));
        this.setBackground(new Color(247, 216, 247));
        this.client = client;
        initialServiceTime.set(client.getServiceTime());
        this.setText("ID: " + client.getID() + "\nArrival time: " + client.getArrivalTime() + "\nService time: " + initialServiceTime + "\nRemaining time: " + client.getRemainingTime());
        this.setEditable(false);
    }
    public void updateClientInfo() {
        this.setText("ID: " + this.client.getID() + "\nArrival time: " + this.client.getArrivalTime() + "\nService time: " + initialServiceTime + "\nRemaining time: " + this.client.getRemainingTime());
    }
}
