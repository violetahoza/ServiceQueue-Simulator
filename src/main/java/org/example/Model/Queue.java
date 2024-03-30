package org.example.Model;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable{
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime, nrClients, waitingTimeSum;

    public Queue(){
        this.waitingTime = new AtomicInteger(0);
        this.clients = new ArrayBlockingQueue<>(500);
        this.nrClients = new AtomicInteger(0);
        this.waitingTimeSum = new AtomicInteger(0);
    }
    public void addClient(Client client){
        clients.add(client);
        nrClients.addAndGet(1);
        waitingTime.addAndGet(client.getServiceTime());
    }
    @Override
    public void run() {
        if(clients.size() != 0){
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

    public AtomicInteger getWaitingTimeSum() {
        return waitingTimeSum;
    }
    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }
}
