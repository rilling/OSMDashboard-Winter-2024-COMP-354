package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;

public class Chairlift {
    private String name;
    private double distance;
    private int waitingTime;
    private double averageSpeed;

    public Chairlift(String name, int distance, int wTime, double aSpeed) {
        this.name = name;
        this.distance = distance;
        this.waitingTime = wTime;
        this.averageSpeed = aSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAscentTime() {
        return distance/averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}