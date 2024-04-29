package de.storchp.opentracks.osmplugin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import org.oscim.android.MapPreferences;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.scalebar.MetricUnitAdapter;
import org.oscim.theme.IRenderTheme;
import org.oscim.theme.StreamRenderTheme;
import org.oscim.theme.ThemeFile;
import org.oscim.theme.VtmThemes;
import org.oscim.theme.ZipRenderTheme;
import org.oscim.theme.ZipXmlThemeResourceProvider;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import de.storchp.opentracks.osmplugin.dashboardapi.APIConstants;
import de.storchp.opentracks.osmplugin.dashboardapi.Track;
import de.storchp.opentracks.osmplugin.dashboardapi.TrackPoint;
import de.storchp.opentracks.osmplugin.dashboardapi.Waypoint;
import de.storchp.opentracks.osmplugin.databinding.ActivityMapsBinding;
import de.storchp.opentracks.osmplugin.maps.MovementDirection;
import de.storchp.opentracks.osmplugin.maps.StyleColorCreator;
import de.storchp.opentracks.osmplugin.utils.MapMode;
import de.storchp.opentracks.osmplugin.utils.MapUtils;
import de.storchp.opentracks.osmplugin.utils.PreferencesUtils;
import de.storchp.opentracks.osmplugin.utils.StatisticElement;
import de.storchp.opentracks.osmplugin.utils.TrackColorMode;
import de.storchp.opentracks.osmplugin.utils.TrackPointsDebug;
import de.storchp.opentracks.osmplugin.utils.TrackStatistics;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
//import android.annotation.NonNull;
//import org.oscim.layers.marker.MarkerItem;
//import org.oscim.core.GeoPoint;
//import org.oscim.utils.MapUnitls;
import org.oscim.core.MapPosition;

//Contributors: MauGomez24, FireMyBoss

/*
* This is just a dummy implementation for the displaying all friends' real time position functionality. The actual implementation will depend on which data
* needs to be pulled from the backend
*/
public class DummyFriendsPosition {

    //Here we have some "friends" that will be moving in the direction of some vector function of the form r(t) = <x(t), y(t)>
    //Starting point on the map is Montreal
    LocationVector v1 = new LocationVector("Friend 1", 73.5674, -45.5019, 0.0001, 0.0001);
    LocationVector v2 = new LocationVector("Friend 2", 73.5674, 45.5019, -0.0001, -0.0001);
    LocationVector v3 = new LocationVector("Friend 3", 73.5674, 45.5019, 0.0003, 0.0002);
    LocationVector v4 = new LocationVector("Friend 4", 73.5674, 45.5019, 0.0005, -0.0003);
    LocationVector v5 = new LocationVector("Friend 5", 73.5674, 45.5019, -0.0002, 0.0001);

    //The friends
    LocationVector[] friends = {v1, v2, v3, v4, v5};

    //getting the friends to move following the slopes and updating the markers on the map
    //we'll run it for 60 seconds as a test
    //WHEN DONE JUST CALL THE METHOD IN MapsActivity.java
    public void displayFriendsTest(){
        try{
            for(int i = 0; i < 61; i++){
                //maybe we can clear all markers first before displaying the new ones
                //pls add this
                for(int j = 0; j < friends.length; j++){
                    friends[j].LinearUpdate();
                    GeoPoint friendLoc = new GeoPoint(friends[j].getLatitude(), friends[j].getLongitude());
                    MarkerItem friendMarker = new MarkerItem("", "", friendLoc);
                    //missing code to add it to map
                }
                Thread.sleep(500);
            }
        }
        catch(InterruptedException e){}
    }


    private class LocationVector{

        private String ID;
        private double latitude;
        private double longitude;
        private double dx;
        private double dy;


        public String getID(){
            return ID;
        }
        public double getLatitude() {
            return latitude;
        }


        public double getLongitude() {
            return longitude;
        }

        public LocationVector(String ID, double latitude, double longitude, double dx, double dy) {
            this.ID = ID;
            this.latitude = latitude;
            this.longitude = longitude;
            this.dx = dx;
            this.dy = dy;
        }

        public void LinearUpdate() {
            this.latitude = this.latitude + this.dx;
            this.longitude = this.longitude + this.dy;
        }
    }


}
