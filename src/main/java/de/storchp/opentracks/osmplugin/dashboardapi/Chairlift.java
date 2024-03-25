package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;

public class Chairlift {
    private String name;
    private double distance;
    private long waitingTime;
    private double averageSpeed;

    public Chairlift(String name, int distance, long wTime, double aSpeed) {
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

    public long getAscentTime() {
        return Math.round(distance/averageSpeed);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}