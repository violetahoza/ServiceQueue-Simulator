package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.Array;
import java.util.*;
import java.util.List;

import static org.example.Model.LogEvents.*;

import org.example.Model.Client;
import org.example.Model.LogEvents;

public class SimulationView extends JFrame {
    private static ArrayList<JPanel> queues = new ArrayList<>(queuesNr.get());
    private JProgressBar progressBar = new JProgressBar();
    private JPanel queuesPanel = new JPanel(), progressPanel = new JPanel();
    private JLabel timeLabel = new JLabel();

    public ArrayList<JPanel> getQueues() {
        return queues;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public SimulationView(String name) {
        super(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        progressPanel.setBackground(new Color(128, 0, 128));
        timeLabel.setText("Current simulation time: " + 0 + "/" + simulationTime.get());
        progressPanel.add(timeLabel);
        progressBar.setMaximum(simulationTime.get());
        progressPanel.add(progressBar);

        this.add(progressPanel, BorderLayout.NORTH);

        queuesPanel.setBackground(new Color(128, 0, 128));
        queuesPanel.setLayout(new GridLayout(queuesNr.get(), 1));
        for(int i = 0; i < queuesNr.get(); i++) {
            JPanel queuePanel = new JPanel();
            queuePanel.setBackground(new Color(128, 0, 128));
            queuePanel.setLayout(new GridLayout(1, clientsNr.get()));
            queuePanel.setSize(1600, 300);
            JLabel queueIndex = new JLabel("Queue " + (i + 1));
            queuePanel.add(queueIndex);
            queuesPanel.add(queuePanel);
            queues.add(queuePanel);
        }
        this.add(queuesPanel, BorderLayout.CENTER);
        this.setVisible(true);
        this.pack();
    }

    public static void addClientToQueue(Client client, int queueIndex) {
        JPanel queuePanel = queues.get(queueIndex);
        JTextArea clientBox = new JTextArea();
        clientBox.setFont(new Font("Arial", Font.BOLD, 15));
        clientBox.setForeground(new Color(128, 0, 128));
        clientBox.setBackground(new Color(247, 216, 247));
        clientBox.setEditable(false);
        clientBox.setText("ID: " + client.getID() + "\nArrival time: " + client.getArrivalTime() + "\nService time: " + client.getServiceTime() + "\nRemaining time: " + client.getRemainingTime());
        queuePanel.add(clientBox);
        queuePanel.revalidate();
        queuePanel.repaint();
    }

    public void updateTime(int currentTime, int maxTime) {
        timeLabel.setText("Current simulation time: " + currentTime + "/" + maxTime);
        progressBar.setValue(currentTime);
    }
    public synchronized void update() {
        for(JPanel panel : queues) {
            if(panel.getComponents().length >= 2) {
                Component[] components = panel.getComponents();
                JTextArea client1 = (JTextArea) components[1];
                client1.setBackground(new Color(220, 200, 250));

                boolean remove = false;
                // Go through all clients in the queue
                for (int i = 1; i < components.length; i++) {
                    JTextArea clientBox = (JTextArea) components[i];
                    String[] clientInfo = clientBox.getText().split("\n");
                    int remainingTime = Integer.parseInt(clientInfo[3].substring(clientInfo[3].lastIndexOf(":") + 2));
                    if (remainingTime == 0)
                        remove = true;
                }
                // Remove client if their service time is completed
                if (remove)
                    panel.remove(1);

                // Update the appearance of the new first element in the queue
                if(panel.getComponents().length > 1) {
                    Component[] updatedComponents = panel.getComponents();
                    JTextArea clientBox2 = (JTextArea) updatedComponents[1];
                    clientBox2.setBackground(new Color(220, 200, 250));
                }
            }
        }
    }

}
