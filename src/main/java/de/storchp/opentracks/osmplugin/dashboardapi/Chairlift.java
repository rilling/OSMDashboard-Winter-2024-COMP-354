package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;

public class Chairlift {
    private String name;
    private double elevationGain;
    private int waitingTime;
    private int totalTimeMoving;
    private double averageSpeed;

    public Chairlift(String name, double eGain, int wTime, int tTimeMoving, double aSpeed) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
        this.averageSpeed = aSpeed;
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

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getTotalTimeMoving() {
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
    // Method to display chairlift information in a table
    // To be replaced with a GUI based table
    public void displayTable() {
        System.out.println("+---------------------------------------+");
        System.out.println("|            Chairlift Info             |");
        System.out.println("+---------------------------------------+");
        System.out.printf("| %-20s | %-10s |\n", "Name", name);
        System.out.printf("| %-20s | %-10.2f |\n", "Elevation Gain", elevationGain);
        System.out.printf("| %-20s | %-10d |\n", "Waiting Time", waitingTime);
        System.out.printf("| %-20s | %-10d |\n", "Total Time Moving", totalTimeMoving);
        System.out.printf("| %-20s | %-10.2f |\n", "Average Speed", averageSpeed);
        System.out.println("+---------------------------------------+");
    }
}
