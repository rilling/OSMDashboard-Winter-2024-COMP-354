package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;

public class Chairlift {
    private String name;
    private double elevationGain;
    private double waitingTime;
    private double totalTimeMoving;
    private double averageSpeed;

    public Chairlift(String name, double eGain, double wTime, double tTimeMoving, double aSpeed) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
        this.averageSpeed = aSpeed;
    }

    public Chairlift(String name, double eGain, double wTime, double tTimeMoving) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public long getWaitingTime() {
        return (long)waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getTotalTimeMoving() {
        return totalTimeMoving;
    }

    public void setTotalTimeMoving(int totalTimeMoving) {
        this.totalTimeMoving = totalTimeMoving;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }
}