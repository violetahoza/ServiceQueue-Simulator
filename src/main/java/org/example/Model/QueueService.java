package org.example.Model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
    public void addClient(Client client){
        clients.add(client);
        nrClients.addAndGet(1);
        waitingTime.addAndGet(client.getServiceTime());
    }
    @Override
    public void run() {
        while(clients.size() != 0){
            if(clients.peek().getServiceTime() == 0){
                LogEvents.log("Client " + clients.peek().getID() + " left");
                try {
                    clients.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (clients.peek().getServiceTime() > 0) {
                LogEvents.log("Client " + clients.peek().getID() + " has the remaining time: " + clients.peek().getServiceTime());
                waitingTime.decrementAndGet();
                clients.peek().decrementServiceTime();
            }
        }
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
