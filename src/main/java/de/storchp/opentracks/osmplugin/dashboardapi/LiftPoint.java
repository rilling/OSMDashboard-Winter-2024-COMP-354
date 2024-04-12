package de.storchp.opentracks.osmplugin.dashboardapi;
import java.sql.Time;
import java.time.LocalTime;

public class LiftPoint {
    private double xCoordinate;
    private double yCoordinate;
    private Time currentTime;

    public LiftPoint() {

    }

    public LiftPoint(double xCoordinate, double yCoordinate, double currentSpeed, Time currentTime) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.currentTime = currentTime;
    }

    public double getXCoordinate() {
        return this.xCoordinate;
    }

    public double getYCoordinate() {
        return this.yCoordinate;
    }

    //public double getCurrentSpeed() {
    //    return this.currentSpeed;
    //}

    public Time getCurrentTime() {
        return this.currentTime;
    }
}