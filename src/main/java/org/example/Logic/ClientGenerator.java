package org.example.Logic;

import org.example.Model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ClientGenerator {
    private final int nrClients; //nr of clients to generate
    private final int minArrival, maxArrival; //range of arrival times
    private final int minService, maxService; //range of service times
    private final Random random;

    public ClientGenerator(int nrClients, int minArrival, int maxArrival, int minService, int maxService){
        this.maxArrival = maxArrival;
        this.maxService = maxService;
        this.nrClients = nrClients;
        this.minArrival = minArrival;
        this.minService = minService;
        this.random = new Random();
    }
    // method to generate clients with random arrival and service times
    public ArrayList<Client> generateClients(){
        ArrayList<Client> clients = new ArrayList<>();
        for(int i = 1; i <= nrClients; i++){
            int arrivalTime = random.nextInt(minArrival,maxArrival + 1); // minArrival is the minimum value for the random integer
            int serviceTime = random.nextInt(minService, maxService + 1); // maxService + 1 is the max value for the random integer (I add +1 because I want maxArrival to be included in the range of possible values)
            clients.add(new Client(i, arrivalTime, serviceTime));
        }
        return clients; //return the list of generated clients
    }
}

