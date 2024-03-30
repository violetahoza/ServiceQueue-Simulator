package org.example.Logic;

import org.example.Model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ClientGenerator {
    private final int nrClients;
    private final int minArrival, maxArrival;
    private final int minService, maxService;
    private final Random random;

    public ClientGenerator(int nrClients, int minArrival, int maxArrival, int minService, int maxService){
        this.maxArrival = maxArrival;
        this.maxService = maxService;
        this.nrClients = nrClients;
        this.minArrival = minArrival;
        this.minService = minService;
        this.random = new Random();
    }

    public ArrayList<Client> generateClients(){
        ArrayList<Client> clients = new ArrayList<>();
        for(int i = 1; i <= nrClients; i++){
            int arrivalTime = random.nextInt(minArrival,maxArrival + 1);
            int serviceTime = random.nextInt(minService, maxService + 1);
            clients.add(new Client(i, arrivalTime, serviceTime));
        }
        return clients;
    }
}

