package de.storchp.opentracks.osmplugin.dashboardapi;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Chairlift {
    private String name;
    private double elevationGain;

    private double waitingTime;
    private double totalTimeMoving;
    private double averageSpeed;

    private ArrayList<TrackPoint> trackPointCollection;
    private double[] lat;
    private double[] lon;

    public Chairlift(String name, double eGain, double wTime, double tTimeMoving, double aSpeed) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
        this.averageSpeed = aSpeed;
        this.lat = new double[trackPointCollection.size()];
        this.lon = new double[trackPointCollection.size()];
    }

    public Chairlift(String name, double eGain, double wTime, double tTimeMoving) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
        this.trackPointCollection = null;
        this.averageSpeed = Math.random() * 10; //to be properly defined later
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

    public long getTotalTimeMoving() {
        return (long)totalTimeMoving;
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

    public List<TrackPoint> getTrackPointCollection() {
        return this.trackPointCollection;
    }

    // Method to get the end point of the run

    //code adapted/taken from Run class to get start point to implement this functionality for chairlift.
    public GeoPoint getStartPoint() {
        if (trackPointCollection != null && !trackPointCollection.isEmpty()) {
            return trackPointCollection.get(0).getLatLong();
        }
        else {
            return null; //or handle this case as per your requirement
        }
    };
    //code adapted/taken from Run class to get the end point to implement this functionality for chairlift.
    public GeoPoint getEndPoint() {
        if (trackPointCollection != null && !trackPointCollection.isEmpty()) {
            return trackPointCollection.get(trackPointCollection.size()-1).getLatLong();
        }
        else {
            return null; //or handle this case as per your requirement
        }
    };
}