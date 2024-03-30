package org.example.Model;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable{
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime, nrClients;

    public Queue(){
        this.waitingTime = new AtomicInteger();
        this.clients = new ArrayBlockingQueue<>(500);
        this.nrClients = new AtomicInteger();
    }
    public void addClient(Client client){
        clients.add(client);
        nrClients.addAndGet(1);
        waitingTime.addAndGet(client.getServiceTime());
    }
    @Override
    public void run() {
        if(clients.size() != 0){

        }
    }

    public AtomicInteger getNrClients() {
        return nrClients;
    }

    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }
}
