package org.example.Model;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueService implements Runnable{
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime, nrClients, totalWaitingTime;

    public QueueService(){
        this.waitingTime = new AtomicInteger(0);
        this.clients = new ArrayBlockingQueue<>(500);
        this.nrClients = new AtomicInteger(0);
        this.totalWaitingTime = new AtomicInteger(0);
    }
    public void addClient(Client client, int timeInQueue){
        clients.add(client); //add client to the queue
        nrClients.addAndGet(1); //update the nr of clients in the queue
        waitingTime.addAndGet(client.getServiceTime() + timeInQueue); //update the waiting time by adding the service time of the client and the time spent in the queue
    }
    @Override
    public void run() {
        //loop through clients in the queue
        for (Client client : clients) {
            if (client.getRemainingTime() == 0) { //if the remaining time for the client is 0, then remove it from the queue
                LogEvents.log("Client " + client.getID() + " left");
                clients.remove(client);
                break; // Exit loop after removing client
            } else if (client.getRemainingTime() > 0) {
                LogEvents.log("Client " + client.getID() + " has the remaining time: " + client.getRemainingTime());
                waitingTime.decrementAndGet();
                //client.decrementRemainingTime();
            }
        }
    }

    // method to get the queue with the shortest total waiting time
    public static QueueService getShortestTimeQueue(ArrayList<QueueService> queues) {
        QueueService shortestQueue = queues.get(0);
        for (QueueService queueService : queues) {
            if (queueService.getWaitingTime().get() < shortestQueue.getWaitingTime().get()) {
                shortestQueue = queueService;
            }
        }
        return shortestQueue;
    }

    // method to get the queue with the minimum number of clients
    public static QueueService getQueueWithMinClients(ArrayList<QueueService> queues) {
        QueueService shortestQueue = queues.get(0);
        for (QueueService queueService : queues) {
            if (queueService.getNrClients().get() < shortestQueue.getNrClients().get()) {
                shortestQueue = queueService;
            }
        }
        return shortestQueue;
    }

    public static QueueService getBestQueue(ArrayList<QueueService> queueServices) {
        QueueService shortestTime = getShortestTimeQueue(queueServices);
        ArrayList<QueueService> shortestTimeQueues = new ArrayList<QueueService>();
        for (QueueService queueService : queueServices) {
            if(queueService.equals(shortestTime))
                shortestTimeQueues.add(queueService);
        }
        return getQueueWithMinClients(shortestTimeQueues);
    }
    public AtomicInteger getNrClients() {
        return nrClients;
    }

    public AtomicInteger getTotalWaitingTime() {
        return totalWaitingTime;
    }
    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }
}
