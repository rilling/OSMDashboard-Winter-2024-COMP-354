package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;
import java.util.ArrayList;

    public class Run {
        private String name;
        private double distance;
        private int duration;
        private List<Chairlift> chairlifts; //chairlifts relevant to each specific run.
        private boolean selected; //Added a boolean field to track the selection state of the run
        private ArrayList<TrackPoint> trackPointCollection;

        // Constructor
        public Run(String name, double distance, int duration, List<Chairlift> chairlifts, ArrayList<TrackPoint> trackPointCollection) {
            this.name = name;
            this.distance = distance;
            this.duration = duration;
            this.chairlifts = chairlifts;
            this.selected = false; //Initialize selected state to false
            this.trackPointCollection = trackPointCollection;
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
        }

    }

