package de.storchp.opentracks.osmplugin.dashboardapi;

import java.util.List;
import java.util.ArrayList;

    public class Run {
        private String name;
        private double distance;
        private int duration;
        private List<Chairlift> chairlifts; //chairlifts relevant to each specific run.
        private boolean selected; //Added a boolean field to track the selection state of the run
        private ArrayList<RunPoint> runPointList;

        // Constructor
        public Run(String name, double distance, int duration, List<Chairlift> chairlifts) {
            this.name = name;
            this.distance = distance;
            this.duration = duration;
            this.chairlifts = chairlifts;
            this.selected = false; //Initialize selected state to false
            this.runPointList = null;
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

        public void createRunPointList() {
            double millisecondsPassed = 0;
            while (true && (millisecondsPassed / (1000 * 60 * 60)) < this.duration { //modify later so that there is a valid activation time-frame condition in place here.
                runPointList.add(new RunPoint()); //how to acquire parameters for determining user location and currentSpeed is unknown at this point.
                Thread.sleep(60000); //wait 60 seconds
                millisecondsPassed += 60000;
            }
        }

    }

