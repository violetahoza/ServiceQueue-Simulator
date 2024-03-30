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

        for(int i = 0; i < queuesNr.get(); i++) {
            queues.add(new QueueService());
            Thread thread = new Thread(queues.get(i));
            thread.start();
        }

        ClientGenerator clientGenerator = new ClientGenerator(clientsNr.get(), minArrival.get(), maxArrival.get(), minService.get(), maxService.get());
        clients.addAll(clientGenerator.generateClients());
        for(int i = 0; i < clientsNr.get(); i++) {
            averageServiceTime += clients.get(i).getServiceTime();
            LogEvents.log("Client " + clients.get(i).getID() + " arrives at: " + clients.get(i).getArrivalTime() + " with service time: " + clients.get(i).getServiceTime());
        }
        averageServiceTime /= (double) clientsNr.get();
    }

    private void addClient(Client client, QueueService queue) {
        if((client.getServiceTime() + queue.getWaitingTime().get() + currentTime) > simulationTime.get()) {
            LogEvents.log("Client " + client.getID() + " can't be added to the queue because there is not enough time.");
        } else {
            queue.addClient(client);
            SimulationView.addClientToQueue(client, queues.indexOf(queue));
            queue.getTotalWaitingTime().addAndGet(queue.getWaitingTime().get());
            queue.getNrClients().addAndGet(1);
            client.setRemainingTime(queue.getWaitingTime().get());
            LogEvents.log("Client " + client.getID() + " was added to queue " + (queues.indexOf(queue) + 1));
        }
    }
    private QueueService bestQueue(){
        // find the best queue based on the lowest waiting time
        int min = Integer.MAX_VALUE;
        QueueService queue = new QueueService();
        // go through all threads to find the best one
        for(QueueService thread : queues){
            if(thread.getWaitingTime().get()< min){
                min = thread.getWaitingTime().get();
                queue = thread;
            }
        }
        return queue;
    }
    @Override
    public synchronized void run() {
        while (currentTime <= simulationTime.get()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogEvents.log(currentTime + ":");
            for (Client client : clients) {
                if(client.getArrivalTime() == currentTime) {
                    QueueService best = bestQueue();
                    addClient(client, best);
                }
                if (client.getRemainingTime() > 0) {
                    client.setRemainingTime(client.getRemainingTime() - 1);
                    view.updateClientRemainingTime(client, client.getRemainingTime());
                }            }
            int peakTime = 0;
            for(QueueService queue : queues) {
                queue.run();
                peakHour += queue.getWaitingTime().get();
            }
            if(peakTime > peakWaitingTime) {
                peakHour = currentTime;
                peakWaitingTime = peakHour;
            }
            view.update();
            view.getProgressBar().setValue(currentTime);
            view.getTimeLabel().setText("Current simulation time: " + currentTime + "/" + simulationTime.get());
            currentTime++;
        }
        for(QueueService queueService : queues) {
            LogEvents.log("Average waiting time for queue " + queues.indexOf(queueService) + ": " + (double) queueService.getTotalWaitingTime().get() / queueService.getNrClients().get());
        }
        LogEvents.log("Average service time: " + averageServiceTime);
        LogEvents.log("Peak hour: " + peakHour + " with average waiting time: " + (double) peakWaitingTime / queuesNr.get());
        SetupView.showMessageDialog("Simulation finished :)", "Simulation status");
        LogEvents.close();
    }
}
