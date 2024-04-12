package org.example.Logic;

import org.example.GUI.*;
import org.example.Model.*;
import static java.lang.Thread.sleep;
import static org.example.Model.LogEvents.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{
    public static AtomicInteger simulationTime = new AtomicInteger(0); // AtomicIntegers to store simulation parameters and states
    public static AtomicInteger clientsNr = new AtomicInteger(0);
    public static AtomicInteger queuesNr = new AtomicInteger(0);
    public static AtomicInteger minArrival = new AtomicInteger(0);
    public static AtomicInteger maxArrival = new AtomicInteger(0);
    public static AtomicInteger minService = new AtomicInteger(0);
    public static AtomicInteger maxService = new AtomicInteger(0);
    private SimulationView view;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<QueueService> queues = new ArrayList<>();
    private int currentTime = 0, peakHour = 0, peakWaitingTime = 0;
    private double averageServiceTime = 0;
    public static enum Strategy{
        SHORTEST_TIME, SHORTEST_QUEUE
    }
    public static Strategy strategy;

    public SimulationManager(SimulationView view) {
        this.view = view;


        // initialize queues
        for(int i = 0; i < queuesNr.get(); i++) {
            queues.add(new QueueService());
            Thread thread = new Thread(queues.get(i));
            thread.start();
        }

        // generate clients
        ClientGenerator clientGenerator = new ClientGenerator(clientsNr.get(), minArrival.get(), maxArrival.get(), minService.get(), maxService.get());
        clients.addAll(clientGenerator.generateClients());
        view.displayClients(clients);
        for(Client client : clients) {
            averageServiceTime += client.getServiceTime();
            LogEvents.log("Client " + client.getID() + " arrives at: " + client.getArrivalTime() + " with service time: " + client.getServiceTime()); // log the client's arrival and service time
        }
        averageServiceTime /= (double) clientsNr.get(); // compute the average service time
    }

    // method to add a client to a queue
    private void addClient(Client client, QueueService queue) {
        if((client.getServiceTime() + queue.getWaitingTime().get() + currentTime) > simulationTime.get()) {
            LogEvents.log("Client " + client.getID() + " can't be added to the queue because there is not enough time.");
        } else {
            queue.addClient(client);
            SimulationView.addClientToQueue(client, queues.indexOf(queue));
            queue.getWaitingTimeSum().addAndGet(queue.getWaitingTime().get());
            //queue.getNrClients().addAndGet(1);
            client.setRemainingTime(queue.getWaitingTime().get());
//            int n = queue.getNrClients().get();
//            if(n == 1)
//                client.setRemainingTime(client.getServiceTime());
//            else if(n > 1)
//            {
//                // Get the previous client in the queue
//                Client previousClient = queue.getClients().stream().skip(n - 2).findFirst().orElse(null);
//                if (previousClient != null) {
//                    client.setRemainingTime(previousClient.getRemainingTime() + client.getServiceTime());
//                }
//            }
            LogEvents.log("Client " + client.getID() + " was added to queue " + (queues.indexOf(queue) + 1));
        }
    }

    @Override
    public synchronized void run() {
        // main simulation loop
        while (currentTime <= simulationTime.get()) {
            try {
                sleep(1000); // wait for 1s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogEvents.log(currentTime + ":");
            for (Client client : clients) { // process each client
                if(client.getArrivalTime() == currentTime) { // if the client has arrived
                    QueueService best = null;
                    if(strategy == Strategy.SHORTEST_QUEUE)
                        best = QueueService.getQueueWithMinClients(queues);
                    if(strategy == Strategy.SHORTEST_TIME)
                        best = QueueService.getShortestTimeQueue(queues);
                    addClient(client, best); // add the client to the optimal queue
                }
                if (client.getRemainingTime() > 0) { // if client is still in the queue
                    client.decrementRemainingTime(); // update the waiting time
                    view.updateClientRemainingTime(client);
                }
            }
            int peakTime = 0;
            for(QueueService queue : queues) { // run each queue
                queue.run();
                peakTime += queue.getWaitingTime().get();
            }
            if(peakTime > peakWaitingTime) {
                peakHour = currentTime; // update peak hour and peak waiting time
                peakWaitingTime = peakTime;
            }
            view.update(); // update the GUI
            view.getProgressBar().setValue(currentTime);
            view.getTimeLabel().setText("Current simulation time: " + currentTime + "/" + simulationTime.get());
            currentTime++;
        }
        // Compute and log the average waiting time for each queue
        ArrayList<Double> avgWaitingTimes = new ArrayList<>();
        for (QueueService queueService : queues) {
            double avgWaitingTime = (double) queueService.getWaitingTimeSum().get() / queueService.getNrClients().get();
            avgWaitingTimes.add(avgWaitingTime);
            LogEvents.log("Average waiting time for queue " + (queues.indexOf(queueService) + 1) + ": " + avgWaitingTime);
        }
        LogEvents.log("Average service time: " + averageServiceTime); // log the average service time and peak hour
        double avgPeakWaitingTime = (double) peakWaitingTime / queuesNr.get();
        LogEvents.log("Peak hour: " + peakHour + " with average waiting time: " +avgPeakWaitingTime );
        SetupView.showMessageDialog("Simulation finished :)", "Simulation status");
        view.displaySimulationResults(avgWaitingTimes, averageServiceTime, peakHour, avgPeakWaitingTime);
        LogEvents.close();
    }
}
