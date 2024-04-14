package org.example.Model;

import org.example.GUI.SimulationView;
import org.example.Logic.SimulationManager;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class QueueService implements Runnable {
    private BlockingQueue<Client> clients;
    private AtomicInteger waitingTime;
    private static volatile boolean isRunning;
    private SimulationView view;
    private SimulationManager manager;
    public static AtomicInteger finishTimes;

    public static void stop() {
        isRunning = false;
    }

    public QueueService(SimulationView view, SimulationManager manager) {
        this.waitingTime = new AtomicInteger(0);
        this.clients = new ArrayBlockingQueue<>(500);
        this.view = view;
        this.isRunning = true;
        this.manager = manager;
        finishTimes = new AtomicInteger(0);
    }

    public void addClient(Client client) {
        clients.add(client); //add client to the queue
        waitingTime.addAndGet(client.getServiceTime()); //update the waiting time by adding the service time of the client to the time spent in the queue
    }

    @Override
    public synchronized void run() {
        while (isRunning) {
            Client client = clients.peek();
            if (client != null) {
                while (client.getServiceTime() > 0) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    client.decrementServiceTime();
                    view.updateClient(client);
                }
                finishTimes.addAndGet(manager.getCurrentTime());
                clients.remove(clients.peek());
                waitingTime.addAndGet(-client.getServiceTime());
                view.update();
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
            if (queueService.getClients().size() < shortestQueue.getClients().size()) {
                shortestQueue = queueService;
            }
        }
        return shortestQueue;
    }

    //    public static QueueService getBestQueue(ArrayList<QueueService> queueServices) {
//        QueueService shortestTime = getShortestTimeQueue(queueServices);
//        ArrayList<QueueService> shortestTimeQueues = new ArrayList<QueueService>();
//        for (QueueService queueService : queueServices) {
//            if(queueService.equals(shortestTime))
//                shortestTimeQueues.add(queueService);
//        }
//        return getQueueWithMinClients(shortestTimeQueues);
//    }

    public AtomicInteger getWaitingTime() {
        return waitingTime;
    }

    public BlockingQueue<Client> getClients() {
        return clients;
    }
    public void displayClients(){
        for(Client client : clients)
        {
            if(client.getServiceTime() != 0)
                LogEvents.log("(" + client.getID() + ", " + client.getArrivalTime() + ", " + client.getServiceTime() + ") ");
        }
    }
}
