package org.example.Logic;

import org.example.Model.QueueService;

import java.util.ArrayList;

public interface StrategyPattern {
    QueueService selectQueue(ArrayList<QueueService> queues);
}
