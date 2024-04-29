package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.ArrayList;
import java.util.List;
import org.oscim.core.GeoPoint;



/**
 * (Collaborated by teams 17 & 18) This class represents a ski run object.
 * It contains methods that are useful in determining a user's statistics
 * about a run they recorded themselves on during a track recording.
 * <p>
 * The methods starting with "calculate" are temporary methods that will
 * be used to generate dummy data about a run that needs to be displayed
 * on OSMDashboard. These methods will be removed once team 7 provides
 * their implementation of these as per their assigned requirements
 *
 * @author serbancaia, danbin, FarnaZ, coureyk
 * @version 2024/03/25
 */
public class Run {
    private String name;
    private double averageSpeed;
    private double maxSpeed; //Going to use Trackpoint.Speed variable
    private int duration;
    private double distance;
    private List<Chairlift> chairlifts; //chairlifts relevant to each specific run.
    private boolean selected; //Added a boolean field to track the selection state of the run
    private ArrayList<TrackPoint> trackPointCollection;
    private double[] lat;
    private double[] lon;

    public Run() {
        this.name = "unnamed";
        this.averageSpeed = 0;
        this.distance = 0;
        this.duration = 0;
        this.maxSpeed = 0;
        this.selected = false; //Initialize selected state to false
        this.lat = new double[trackPointCollection.size()];
        this.lon = new double[trackPointCollection.size()];
    }

    // Constructor
    public Run(String name, double distance, int duration, List<Chairlift> chairlifts, ArrayList<TrackPoint> trackPointCollection) {
        this.name = name;
        this.distance = distance;
        this.duration = duration;
        this.chairlifts = chairlifts;
        this.selected = false; //Initialize selected state to false
        this.trackPointCollection = trackPointCollection;
        this.lat = new double[trackPointCollection.size()];
        this.lon = new double[trackPointCollection.size()];
    }

    public Run(String name, double avgSpeed, double distance, int duration, double maxSpeed) {
        this.name = name;
        this.averageSpeed = avgSpeed;
        this.distance = distance;
        this.duration = duration;
        this.maxSpeed = maxSpeed;
        this.selected = false; //Initialize selected state to false
    }

    public Run(String name, double avgSpeed, double distance, int duration, double maxSpeed, List<Chairlift> chairlifts, ArrayList<TrackPoint> trackPointCollection) {
        this.name = name;
        this.averageSpeed = avgSpeed;
        this.distance = distance;
        this.duration = duration;
        this.maxSpeed = maxSpeed;
        this.selected = false; //Initialize selected state to false
        this.trackPointCollection = trackPointCollection;
        this.lat = new double[trackPointCollection.size()];
        this.lon = new double[trackPointCollection.size()];
    }

    public Run(String name, double avgSpeed, double distance, int duration, double maxSpeed, List<Chairlift> chairlifts) {
        this.name = name;
        this.averageSpeed = avgSpeed;
        this.distance = distance;
        this.duration = duration;
        this.maxSpeed = maxSpeed;
        this.chairlifts = chairlifts;
        this.selected = false; //Initialize selected state to false
        this.trackPointCollection = null;
    }

    public Run(Run runObj) {
        this.name = runObj.name;
        this.averageSpeed = runObj.averageSpeed;
        this.distance = runObj.distance;
        this.duration = runObj.duration;
        this.maxSpeed = runObj.maxSpeed;
        this.chairlifts = runObj.chairlifts;
        this.selected = runObj.selected;
        this.trackPointCollection = runObj.trackPointCollection;
    }

    //Getters
    public String getName() {
        return this.name;
    }

    public double getAverageSpeed() {
        return this.averageSpeed;
    }

    public double getDistance() {
        return this.distance;
    }

    public int getDuration() {
        return this.duration;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public boolean isSelected() {
        return selected;
    }

    public List<TrackPoint> getTrackPointCollection() { return this.trackPointCollection; }

    //Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAverageSpeed(int avgSpeed) {
        this.averageSpeed = avgSpeed;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    // Setter for the selected state
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Method to mark the run as selected
    public void select() {
        this.selected = true;
        for (int i = 0; i < trackPointCollection.size(); i++) {
            lat[i] = trackPointCollection.get(i).getLatLong().getLatitude();
            lon[i] = trackPointCollection.get(i).getLatLong().getLongitude();
        }
    }
    //invoke paintRun

    /**
     * The real data  for the total run time is suppose to be fetched from a "statistic team".
     * For now, we will implement a small algorithm to calculate time and will use Dummy data
     * for Sprint 2
     */
    private double calculateTotalRunTime(List<TrackPoint> trackPointCollection) {
        double returnValue;

        TrackPoint firstPoint = trackPointCollection.get(0);
        TrackPoint lastPoint = trackPointCollection.get(trackPointCollection.size() - 1);

        returnValue = Math.abs(firstPoint.getTimeMillis() - lastPoint.getTimeMillis());

        return returnValue;
    }

    /**
     * Method to calculate the max speed
     * Important to know that on OPENTRACKS at class TrackStatistics have an attribute called
     * total Duration time
     */

    private double calculateMaxSpeed(List<TrackPoint> trackPointCollection) {
        double maximumSpeed = trackPointCollection.get(0).getSpeed();

        for (int i = 0; i < trackPointCollection.size(); i++) {
            if (maximumSpeed < trackPointCollection.get(i).getSpeed()) {
                maximumSpeed = trackPointCollection.get(i).getSpeed();
            }
        }

        return maximumSpeed;
    }

    /**
     * Calculates the average speed of an entire run given a list of TrackPoints belonging to a run
     * the user skied on (km/h).
     *
     * @param trackPointCollection List of TrackPoints belonging to a run
     * @return user's average speed during the entire run in km/h
     * @author serbancaia
     */
    private double calculateAverageSpeedKmPerHour(List<TrackPoint> trackPointCollection) {
        double averageRunSpeed = 0;
        int countSegments = -1;

        for (TrackPoint trackPoint : trackPointCollection) {
            if (countSegments > -1) {
                averageRunSpeed += trackPoint.getSpeed();
            }
            countSegments++;
        }

        if (countSegments < 1)
            return 0;
        else
            return averageRunSpeed / countSegments;
    }

    /**
     * Calculates the distance of an entire run given a list of TrackPoints belonging to a run
     * the user skied on (meters).
     * @param trackPointCollection List of TrackPoints belonging to a run
     * @return user's travelled distance during the entire run in meters
     * @author serbancaia
     */
    private double calculateDistanceInMeters(List<TrackPoint> trackPointCollection) {
        double distanceSum = 0;
        TrackPoint trackPoint1 = null;
        TrackPoint trackPoint2 = null;

        for (TrackPoint i : trackPointCollection) {
            if (i == null) {
                continue;
            }
            if (trackPoint1 == null) {
                trackPoint1 = i;
            } else if (trackPoint2 == null) {
                trackPoint2 = i;
                distanceSum += trackPoint1.getLatLong().sphericalDistance(trackPoint2.getLatLong());
            } else {
                trackPoint1 = trackPoint2;
                trackPoint2 = i;
                distanceSum += trackPoint1.getLatLong().sphericalDistance(trackPoint2.getLatLong());
            }
        }

        return distanceSum;
    }

    /**
     * Retrieves the start point of the run.
     * @return The GeoPoint representing the start point of the run, or null if no track points are available.
     * @author FarnaZ
     */
    public GeoPoint getStartPoint() {
        if (trackPointCollection != null && !trackPointCollection.isEmpty()) {
            return trackPointCollection.get(0).getLatLong();
        } else {
            return null; // Or handle this case as per your requirement
        }
    }

    /**
     * Retrieves the end point of the run.
     * @return The GeoPoint representing the end point of the run, or null if no track points are available.
     * @author FarnaZ
     */
    public GeoPoint getEndPoint() {
        if (trackPointCollection != null && !trackPointCollection.isEmpty()) {
            return trackPointCollection.get(trackPointCollection.size() - 1).getLatLong();
        } else {
            return null; // Or handle this case as per your requirement
        }
    }

    public GeoPoint getPoint(int i) {
        if (trackPointCollection != null && i < trackPointCollection.size()) {
            return trackPointCollection.get(i).getLatLong();
        } else {
            return null; // Or handle this case as epr your requirement
        }
    }
}

