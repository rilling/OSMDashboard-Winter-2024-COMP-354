package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;


/**
 * we need:
 * name: the name of the segment
 * distance: the distance covered in the segment
 * time: the time taken in this segment
 * speed: the speed during that segment
 * slop: the average slop during the segment
 */
public class Segment {
    private String name;
    private double distance; // in meters

    private long time; // in seconds

    private double slope;

    public Segment(String name, int distance, long time, double slope) {
        this.name = name;
        this.distance = distance;
        this.time = time;
        this.slope = slope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getSpeed() {
        return this.distance/this.time;
    }

}