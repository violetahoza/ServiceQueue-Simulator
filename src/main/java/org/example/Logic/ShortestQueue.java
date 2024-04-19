package org.example.Logic;

import org.example.Model.QueueService;

import java.util.ArrayList;

public class ShortestQueue implements StrategyPattern{
    @Override
    public QueueService selectQueue(ArrayList<QueueService> queues) {
        QueueService shortestQueue = queues.get(0);
        for (QueueService queueService : queues) {
            if (queueService.getClients().size() < shortestQueue.getClients().size()) {
                shortestQueue = queueService;
            }
        }
        return shortestQueue;
    }
}
