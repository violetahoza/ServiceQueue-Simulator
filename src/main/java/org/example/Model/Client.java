package org.example.Model;

public class Client {
    private final int ID, arrivalTime;
    private int serviceTime, remainingTime;

    public Client(int id, int arrivalTime, int serviceTime) {
        this.ID = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getID() {
        return ID;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getServiceTime() {
        return serviceTime;
    }
    public int getRemainingTime() {
        return remainingTime;
    }
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
    public void decrementRemainingTime(){
        this.remainingTime--;
    }
    public void decrementServiceTime(){
        this.serviceTime--;
    }

}
