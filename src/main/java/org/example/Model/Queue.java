package org.example.Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Queue implements Runnable{
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime, nrClients;

    @Override
    public void run() {

    }
}
