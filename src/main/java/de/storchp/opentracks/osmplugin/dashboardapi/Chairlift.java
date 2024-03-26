package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.ArrayList;
import java.util.List;

public class Chairlift {
    private String name;
    private double elevationGain;
    private int waitingTime;
    private int totalTimeMoving;
    private double averageSpeed;

    private boolean selected; //add variable to allow user to select chairlift
    private ArrayList<TrackPoint> trackPointCollection;
    private double lat[]; //array to hold lats of chairlifts
    private double lon[]; //array to hold lons of chairlifts

    public Chairlift(String name, double eGain, int wTime, int tTimeMoving, double aSpeed) {
        this.name = name;
        this.elevationGain = eGain;
        this.waitingTime = wTime;
        this.totalTimeMoving = tTimeMoving;
        this.averageSpeed = aSpeed;

        this.lat = new double[this.trackPointCollection.size()];
        this.lon = new double[this.trackPointCollection.size()];
        this.selected = false; //default is a charlift is not selected.
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    //select specific chairlift and highlight it.
    public void select() {
        this.selected = true;
    }

    //this method extracts the coordinates (lat and lon) for each chairlift, which will allow us to highlight the
    //chairlifts in different colors later with the future implementation planned below.
    //
    public void extract_coordinates_chairlifts() {
        for(int i = 0; i < trackPointCollection.size(); i++){
            lat[i] = trackPointCollection.get(i).getLatLong().getLatitude();
            lon[i] = trackPointCollection.get(i).getLatLong().getLongitude();
        }
    }

    //Highlight the chairlifts, will be implemented in future sprint.
    /**
     * This code is commented out to avoid any problems with the code.
     *  *    This code will be implemented in future sprints, it gives an idea on how we can make
     *  *     each run a different color, using the x and y coordinates for the run. Have not implemented
     *  *     yet because we need to figure out how to do it on a map and not just in a frame.
     *  * The code may be different later based on more research, but similar to this idea.
     *  *
     *     class drawingComponent extends JComponent {
     *         public void paintComponent(Graphics g) {
     *             Graphics2D g2 = (Graphics2D) g;
     *             g2.setStroke(new BasicStroke(7));
     *
     *             double[] x2Points = {0,100,200,300,400,500}; //This data would be replaced with lat lon
     *             double[] y2Points = {250,20,100,40,50,60};
     *
     *             for (int i = 0; i < x2Points.length - 1; i++) {
     *                 Line2D.Double line = new Line2D.Double(x2Points[i], y2Points[i], x2Points[i+1], y2Points[i+1]);
     *                 switch (i) {
     *                     case 0:
     *                         g2.setPaint(Color.BLACK);
     *                         break;
     *                     case 1:
     *                         g2.setPaint(Color.BLUE);
     *                         break;
     *                     case 2:
     *                         g2.setPaint(Color.CYAN);
     *                         break;
     *                     case 3:
     *                         g2.setPaint(Color.DARK_GRAY);
     *                         break;
     *                     case 4:
     *                         g2.setPaint(Color.GRAY);
     *                         break;
     *                     case 5:
     *                         g2.setPaint(Color.GREEN);
     *                         break;
     *                     default:
     *                         g2.setPaint(Color.MAGENTA);
     *                 }
     *                 g2.draw(line);
     *             }
     *         }
     *     }
     */


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
