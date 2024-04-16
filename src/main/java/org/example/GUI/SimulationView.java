package org.example.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static org.example.Logic.SimulationManager.*;
import org.example.Model.Client;

public class SimulationView extends JFrame {
    private static ArrayList<JPanel> queues = new ArrayList<>(queuesNr);
    private JProgressBar progressBar = new JProgressBar();
    private JPanel queuesPanel = new JPanel(), progressPanel = new JPanel(), clientsPanel = new JPanel();
    private JLabel timeLabel = new JLabel();

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

        //setup progress panel
        progressPanel.setBackground(new Color(128, 0, 128));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        timeLabel.setText("Current simulation time: " + 0 + "/" + simulationTime);
        timeLabel.setForeground(new Color(220, 200, 250));
        progressPanel.add(timeLabel);
        progressBar.setMaximum(simulationTime);
        progressPanel.add(progressBar);
        this.add(progressPanel, BorderLayout.NORTH);

        //setup queues panel
        queuesPanel.setBackground(new Color(128, 0, 128));
        queuesPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        queuesPanel.setLayout(new GridLayout(queuesNr, 1));
        for(int i = 0; i < queuesNr; i++) {
            JPanel queuePanel = new JPanel();
            queuePanel.setBackground(new Color(128, 0, 128));
            queuePanel.setLayout(new GridLayout(1, clientsNr));
            queuePanel.setSize(1600, 100);
            JLabel queueIndex = new JLabel("Queue " + (i + 1));
            queueIndex.setForeground(new Color(220, 200, 250));
            queuePanel.add(queueIndex);
            queuesPanel.add(queuePanel);
            queues.add(queuePanel);
        }
        this.add(queuesPanel, BorderLayout.CENTER);

        // Setup clients panel
        clientsPanel.setBackground(new Color(128, 0, 128));
        clientsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        clientsPanel.setLayout(new GridLayout(0, 1));
        JScrollPane clientsScrollPane = new JScrollPane(clientsPanel);
        this.add(clientsScrollPane, BorderLayout.WEST);

        this.setMinimumSize(new Dimension(1600, 900));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.pack();
    }
    public void displayClients(ArrayList<Client> clients) {
        Collections.sort(clients, Comparator.comparingInt(Client::getArrivalTime));
        for (Client client : clients) {
            JTextArea clientBox = new JTextArea();
            clientBox.setFont(new Font("Arial", Font.BOLD, 15));
            clientBox.setForeground(new Color(128, 0, 128));
            clientBox.setBackground(new Color(247, 216, 247));
            clientBox.setSize(200, 100);
            clientBox.setEditable(false);
            clientBox.setText("ID: " + client.getID() + "\nArrival time: " + client.getArrivalTime() + "\nService time: " + client.getServiceTime());
            clientsPanel.add(clientBox);
        }
        clientsPanel.revalidate();
        clientsPanel.repaint();
    }
    public synchronized static void addClientToQueue(Client client, int queueIndex) {
        JPanel queuePanel = queues.get(queueIndex);
        JTextArea clientBox = new JTextArea();
        clientBox.setFont(new Font("Arial", Font.BOLD, 15));
        clientBox.setForeground(new Color(128, 0, 128));
        clientBox.setBackground(new Color(247, 216, 247));
        clientBox.setSize(200, 100);
        clientBox.setEditable(false);
        clientBox.setText("ID: " + client.getID() + "\nArrival time: " + client.getArrivalTime() + "\nService time: " + client.getServiceTime());
        queuePanel.add(clientBox);
        queuePanel.revalidate();
        queuePanel.repaint();
    }
    public void displaySimulationResults(double avgWaitingTime, double avgServiceTime, int peakHour, int peakMax) {
        StringBuilder message = new StringBuilder("Average waiting time: " +  avgWaitingTime + "\n");
//        for (int i = 0; i < avgWaitingTimes.size(); i++) {
//            message.append("Queue ").append(i + 1).append(": ").append(avgWaitingTimes.get(i)).append("\n");
//        }
        message.append("Average service time: " + avgServiceTime + "\nPeak hour: " + peakHour + " with the max nr of clients: " + peakMax);
        JOptionPane.showMessageDialog(this, message.toString(), "Simulation Results", JOptionPane.INFORMATION_MESSAGE);
    }
    public synchronized void updateClient(Client client) {
        for (JPanel queuePanel : queues) {
            for (Component component : queuePanel.getComponents()) {
                if (component instanceof JTextArea) {
                    JTextArea clientBox = (JTextArea) component;
                    String[] clientInfo = clientBox.getText().split("\n");
                    if (clientInfo.length > 0 && clientInfo[0].trim().length() > 0 && Integer.parseInt(clientInfo[0].substring(clientInfo[0].lastIndexOf(":") + 2)) == client.getID()) {
                        clientBox.setText("ID: " + client.getID() + "\nArrival time: " + client.getArrivalTime() + "\nService time: " + client.getServiceTime());
                        break;
                    }
                }
            }
        }
    }
//    public void updateTime(int currentTime, int maxTime) {
//        timeLabel.setText("Current simulation time: " + currentTime + "/" + maxTime);
//        progressBar.setValue(currentTime);
//    }
    public synchronized void update() {
        for(JPanel panel : queues) {
            if(panel.getComponents().length >= 2) {
                Component[] components = panel.getComponents();
                JTextArea client1 = (JTextArea) components[1];
                client1.setBackground(new Color(220, 200, 250));

                boolean remove = false;
                // go through all clients in the queue
                for (int i = 1; i < components.length; i++) {
                    JTextArea clientBox = (JTextArea) components[i];
                    String[] clientInfo = clientBox.getText().split("\n");
                    int remainingTime = 0;
                    if (clientInfo.length > 2) {
                        remainingTime = Integer.parseInt(clientInfo[2].substring(clientInfo[2].lastIndexOf(":") + 2));
                    }
                    if (remainingTime == 0) {
                        remove = true;
                        panel.remove(1); // remove the client if the service time is completed
                        break;
                    }
                }
                if (remove) {
                    panel.revalidate();
                    panel.repaint();
                }

                // update the appearance of the new first element in the queue
                if(panel.getComponents().length > 1) {
                    Component[] updatedComponents = panel.getComponents();
                    JTextArea clientBox2 = (JTextArea) updatedComponents[1];
                    clientBox2.setBackground(new Color(220, 200, 250));
                }
            }
        }
    }
}
