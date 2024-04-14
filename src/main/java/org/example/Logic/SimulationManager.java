package org.example.Logic;

import org.example.GUI.*;
import org.example.Model.*;

import static java.lang.Thread.sleep;
import static org.example.Model.LogEvents.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    public static AtomicInteger simulationTime = new AtomicInteger(0);
    public static AtomicInteger clientsNr = new AtomicInteger(0);
    public static AtomicInteger queuesNr = new AtomicInteger(0);
    public static AtomicInteger minArrival = new AtomicInteger(0);
    public static AtomicInteger maxArrival = new AtomicInteger(0);
    public static AtomicInteger minService = new AtomicInteger(0);
    public static AtomicInteger maxService = new AtomicInteger(0);
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
    }
    private void initializeQueues() {
        for (int i = 0; i < queuesNr.get(); i++) {
            QueueService queue = new QueueService(view, this);
            queues.add(queue);
            Thread thread = new Thread(queue);
            thread.start();
        }
    }

    private void generateClients() {
        ClientGenerator clientGenerator = new ClientGenerator(clientsNr.get(), minArrival.get(), maxArrival.get(), minService.get(), maxService.get());
        clients.addAll(clientGenerator.generateClients());
        view.displayClients(clients);
        for (Client client : clients) {
            averageServiceTime += client.getServiceTime();
            arrivalsTime += client.getArrivalTime();
            LogEvents.log("Client " + client.getID() + " arrives at: " + client.getArrivalTime() + " with service time: " + client.getServiceTime() + "\n");
        }
        averageServiceTime /= (double) clientsNr.get();
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
        view.getTimeLabel().setText("Current simulation time: " + currentTime + "/" + simulationTime.get());
        currentTime++;
    }

    @Override
    public synchronized void run() {
        // main simulation loop
        while (currentTime <= simulationTime.get()) {
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
            for (QueueService queue : queues) { // run each queue
                peak += queue.getClients().size();
            }
            if (peak > peakMax) {
                peakHour = currentTime; // update peak hour and peak waiting time
                peakMax = peak;
            }

            for (int i = 0; i < queuesNr.get(); i++) {
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

        for (QueueService queue : queues)
            queue.stop();

        averageWaitingTime = (double) (QueueService.finishTimes.get() - arrivalsTime) / clientsNr.get();

        // Compute and log the average waiting time for each queue
        LogEvents.log("\n\nAverage waiting time: " + averageWaitingTime);
        LogEvents.log("\nAverage service time: " + averageServiceTime); // log the average service time and peak hour
        LogEvents.log("\nPeak hour: " + peakHour + " with the max nr of clients: " + peakMax);
        SetupView.showMessageDialog("Simulation finished :)", "Simulation status");
        view.displaySimulationResults(averageWaitingTime, averageServiceTime, peakHour, peakMax);
    }
}
