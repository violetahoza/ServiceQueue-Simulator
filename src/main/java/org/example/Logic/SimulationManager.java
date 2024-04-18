package org.example.Logic;

import org.example.GUI.*;
import org.example.Model.*;

import static java.lang.Thread.sleep;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    public static int simulationTime = 0;
    public static int clientsNr = 0;
    public static int queuesNr = 0;
    public static int minArrival = 0;
    public static int maxArrival = 0;
    public static int minService = 0;
    public static int maxService = 0;
    private SimulationView view;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<QueueService> queues = new ArrayList<>();
    private int currentTime = 0, peakHour = 0, peakMax = 0, arrivalsTime = 0;
    private double averageServiceTime = 0, averageWaitingTime = 0.0;

    public static enum Strategy {
        SHORTEST_TIME, SHORTEST_QUEUE
    }

    public static Strategy strategy;

    public SimulationManager(SimulationView view) {
        this.view = view;
        initializeQueues();
        generateClients();
    }

    public int getCurrentTime(){
        return this.currentTime;
    } // get the current simulation time
    private void initializeQueues() {
        for (int i = 0; i < queuesNr; i++) {
            QueueService queue = new QueueService(view, this);
            queues.add(queue);
            Thread thread = new Thread(queue);
            thread.start();
        }
    }

    private void generateClients() {
        ClientGenerator clientGenerator = new ClientGenerator(clientsNr, minArrival, maxArrival, minService, maxService);
        clients.addAll(clientGenerator.generateClients());
        view.displayClients(clients);
        for (Client client : clients) {
            averageServiceTime += client.getServiceTime();
            arrivalsTime += client.getArrivalTime();
            LogEvents.log("Client " + client.getID() + " arrives at: " + client.getArrivalTime() + " with service time: " + client.getServiceTime() + "\n");
        }
        averageServiceTime /= (double) clientsNr;
    }

    // method to add a client to a queue
    private void addClient(Client client, QueueService queue) {
//        if((client.getServiceTime() + queue.getWaitingTime().get() + currentTime) > simulationTime.get()) {
//            LogEvents.log("Client " + client.getID() + " can't be added to the queue because there is not enough time.");
//        } else {
        queue.addClient(client);
        SimulationView.addClientToQueue(client, queues.indexOf(queue));
        //LogEvents.log("Client " + client.getID() + " was added to queue " + (queues.indexOf(queue) + 1));
        // }
    }

    public synchronized void updateCurrentTime() {
        view.getProgressBar().setValue(currentTime);
        view.getTimeLabel().setText("Current simulation time: " + currentTime + "/" + simulationTime);
        currentTime++;
    }

    @Override
    public synchronized void run() {
        // main simulation loop
        while (currentTime <= simulationTime) {
            LogEvents.log("\n" + currentTime + ":\nWaiting clients: ");
            for (Client client : clients) { // process each client
                if (client.getArrivalTime() > currentTime) {
                    LogEvents.log(("(") + client.getID() + ", " + client.getArrivalTime() + ", " + client.getServiceTime() + ") ");
                }
                else if (client.getArrivalTime() == currentTime) { // if the client has arrived
                    QueueService best = null;
                    if (strategy == Strategy.SHORTEST_QUEUE)
                        best = QueueService.getQueueWithMinClients(queues);
                    if (strategy == Strategy.SHORTEST_TIME)
                        best = QueueService.getShortestTimeQueue(queues);
                    addClient(client, best); // add the client to the optimal queue
                }
            }

            int peak = 0;
            for (QueueService queue : queues) {
                peak += queue.getClients().size();
            }
            if (peak > peakMax) {
                peakHour = currentTime; // update the peak hour
                peakMax = peak;
            }

            // log queue status and update simulation view
            for (int i = 0; i < queuesNr; i++) {
                if (queues.get(i).getClients().isEmpty())
                    LogEvents.log("\nQueue " + (i + 1) + ": closed");
                else {
                    LogEvents.log("\nQueue " + (i + 1) + ": ");
                    queues.get(i).displayClients();
                }
            }
            LogEvents.log("\n");
            view.update();
            updateCurrentTime();

            try {
                sleep(1000); // wait for 1s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(QueueService queue : queues)
            if(!queue.getClients().isEmpty())
                for(Client client : queue.getClients())
                    QueueService.addToFinishTime(simulationTime);

        // stop all the threads after the simulation ends
        for (QueueService queue : queues)
            queue.stop();

        averageWaitingTime = (double) (QueueService.finishTimes.get() - arrivalsTime) / clientsNr;

        // Compute and log the average waiting time for each queue
        LogEvents.log("\n\nAverage waiting time: " + averageWaitingTime);
        LogEvents.log("\nAverage service time: " + averageServiceTime); // log the average service time and peak hour
        LogEvents.log("\nPeak hour: " + peakHour + " with the max nr of clients: " + peakMax);
        SetupView.showMessageDialog("Simulation finished :)", "Simulation status");
        view.displaySimulationResults(averageWaitingTime, averageServiceTime, peakHour, peakMax);
    }
//    private boolean allClientsLeft() {
//        for (QueueService queue : queues) {
//            if (!queue.getClients().isEmpty()) {
//                return false; // If any queue still has clients, return false
//            }
//        }
//        return true; // If all queues are empty, return true
//    }
}
