package org.example.Logic;

import org.example.GUI.*;
import org.example.Model.*;
import static java.lang.Thread.sleep;
import static org.example.Model.LogEvents.*;
import java.util.*;

public class SimulationManager implements Runnable{
    private SimulationView view;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<QueueService> queues = new ArrayList<>();
    private int currentTime = 0, peakHour = 0, peakWaitingTime = 0;
    private double averageServiceTime = 0;

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
            queue.addClient(client, queue.getWaitingTime().get());
            SimulationView.addClientToQueue(client, queues.indexOf(queue));
            queue.getTotalWaitingTime().addAndGet(queue.getWaitingTime().get());
            queue.getNrClients().addAndGet(1);
            client.setRemainingTime(queue.getWaitingTime().get());
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
                    QueueService best = QueueService.getBestQueue(queues);
                    addClient(client, best); // add the client to the optimal queue
                }
                if (client.getRemainingTime() > 0) { // if client is still in the queue
                    client.decrementRemainingTime(); // update the waiting time
                    view.updateClientRemainingTime(client, client.getRemainingTime());
                }            }
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
        for(QueueService queueService : queues) { //compute and log the average waiting time for each queue
            LogEvents.log("Average waiting time for queue " + (queues.indexOf(queueService) + 1) + ": " + (double) queueService.getTotalWaitingTime().get() / queueService.getNrClients().get());
        }
        LogEvents.log("Average service time: " + averageServiceTime); // log the average service time and peak hour
        LogEvents.log("Peak hour: " + peakHour + " with average waiting time: " + (double) peakWaitingTime / queuesNr.get());
        SetupView.showMessageDialog("Simulation finished :)", "Simulation status");
        LogEvents.close();
    }
}
