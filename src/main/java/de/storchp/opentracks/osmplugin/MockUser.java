package de.storchp.opentracks.osmplugin;

import org.oscim.core.GeoPoint;

//Temporary user class while waiting for the other team
class MockUser {
    String name;
    GeoPoint location;
    String currentResort;
    Boolean isAlert;

    public MockUser(String name, GeoPoint location, String currentResort) {
        this.name = name;
        this.location = location;
        this.currentResort = currentResort;
        this.isAlert = false;
    }

    public String getName() {
        return name;
    }

    public String getCurrentResort() {
        return currentResort;
    }

    public String getLocation() {
        return location.getLatitude()+", " + location.getLongitude()+"";
    }

    public Boolean getIsAlert() {
        return isAlert;
    }

    public void wasAlerted() {
        isAlert = true;
    }
}