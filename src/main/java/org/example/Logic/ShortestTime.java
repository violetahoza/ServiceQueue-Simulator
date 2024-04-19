package org.example.Logic;

import org.example.Model.QueueService;

import java.util.ArrayList;

public class ShortestTime implements StrategyPattern{
    @Override
    public QueueService selectQueue(ArrayList<QueueService> queues) {
        QueueService shortestQueue = queues.get(0);
        for (QueueService queueService : queues) {
            if (queueService.getWaitingTime().get() < shortestQueue.getWaitingTime().get()) {
                shortestQueue = queueService;
            }
        }
        return shortestQueue;
    }
}
