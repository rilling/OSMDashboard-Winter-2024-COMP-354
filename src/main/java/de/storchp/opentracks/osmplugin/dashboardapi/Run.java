package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;
import java.util.ArrayList;
import org.oscim.core.GeoPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

    public class Run {
        private String name;
        private double distance;
        private int duration;
        private List<Chairlift> chairlifts; //chairlifts relevant to each specific run.
        private boolean selected; //Added a boolean field to track the selection state of the run
        private ArrayList<TrackPoint> trackPointCollection;
        private double[] lat;
        private double[] lon;

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

        public boolean isSelected() {
            return selected;
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
    }

    class drawingRun extends JComponent {
    public void paintRun(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(7));

        double[] x2Points = {0,100,200,300,400,500}; //replace dummy data with lat array at future date
        double[] y2Points = {250,20,100,40,50,60}; //replace dummy data with long array at future date

        for (int i = 0; i < x2Points.length - 1; i++) {
            Line2D.Double line = new Line2D.Double(x2Points[i], y2Points[i], x2Points[i+1], y2Points[i+1]);
            switch (i) {
                case 0:
                    g2.setPaint(Color.BLACK);
                    break;
                case 1:
                    g2.setPaint(Color.BLUE);
                    break;
                case 2:
                    g2.setPaint(Color.CYAN);
                    break;
                case 3:
                    g2.setPaint(Color.DARK_GRAY);
                    break;
                case 4:
                    g2.setPaint(Color.GRAY);
                    break;
                case 5:
                    g2.setPaint(Color.GREEN);
                    break;
                default:
                    g2.setPaint(Color.MAGENTA);
            }
            g2.draw(line);
        }
    }
}

