package de.storchp.opentracks.osmplugin.dashboardapi;
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
    private double speed; // in meters

    private long time; // in seconds

    private double slope;

    public Segment(String name, double speed, long time, double slope) {
        this.name = name;
        this.speed = speed;
        this.time = time;
        this.slope = slope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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

}