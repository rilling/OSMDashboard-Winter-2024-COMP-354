package de.storchp.opentracks.osmplugin;


import static android.util.TypedValue.COMPLEX_UNIT_PT;
import static org.oscim.map.Viewport.MIN_ZOOM_LEVEL;
import static java.util.Comparator.comparingInt;


import org.oscim.backend.AssetAdapter;
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.map.Viewport;
import org.oscim.theme.IRenderTheme.ThemeException;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ConcatAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
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
import org.oscim.layers.tile.buildings.S3DBLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.map.Viewport;
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
import org.oscim.theme.XmlRenderThemeMenuCallback;
import org.oscim.theme.XmlThemeResourceProvider;
import org.oscim.theme.ZipRenderTheme;
import org.oscim.theme.ZipXmlThemeResourceProvider;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.bitmap.DefaultSources;
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mapfile.MultiMapFileTileSource;
import org.oscim.tiling.source.overpass.OverpassTileSource;

import org.oscim.theme.ThemeLoader;
import org.oscim.tiling.TileSource.OpenResult;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipInputStream;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

import de.storchp.opentracks.osmplugin.dashboardapi.APIConstants;
import de.storchp.opentracks.osmplugin.dashboardapi.Chairlift;
import de.storchp.opentracks.osmplugin.dashboardapi.ChairliftAdapter;
import de.storchp.opentracks.osmplugin.dashboardapi.Run;
import de.storchp.opentracks.osmplugin.dashboardapi.RunAdapter;
import de.storchp.opentracks.osmplugin.dashboardapi.Segment;
import de.storchp.opentracks.osmplugin.dashboardapi.SegmentAdapter;
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





public class MapsActivity extends BaseActivity implements ItemizedLayer.OnItemGestureListener<MarkerInterface> {

    private static final String TAG = MapsActivity.class.getSimpleName();
    public static final String EXTRA_MARKER_ID = "marker_id";
    private static final int MAP_DEFAULT_ZOOM_LEVEL = 12;
    private static final String EXTRAS_PROTOCOL_VERSION = "PROTOCOL_VERSION";
    private static final String EXTRAS_OPENTRACKS_IS_RECORDING_THIS_TRACK = "EXTRAS_OPENTRACKS_IS_RECORDING_THIS_TRACK";
    private static final String EXTRAS_SHOULD_KEEP_SCREEN_ON = "EXTRAS_SHOULD_KEEP_SCREEN_ON";
    private static final String EXTRAS_SHOW_WHEN_LOCKED = "EXTRAS_SHOULD_KEEP_SCREEN_ON";
    private static final String EXTRAS_SHOW_FULLSCREEN = "EXTRAS_SHOULD_FULLSCREEN";
    private boolean isOpenTracksRecordingThisTrack;
    private ActivityMapsBinding binding;
    private Map map;
    private MapPreferences mapPreferences;
    private IRenderTheme renderTheme;
    private BoundingBox boundingBox;
    private GroupLayer polylinesLayer;
    private ItemizedLayer waypointsLayer;
    private long lastWaypointId = 0;
    private long lastTrackPointId = 0;
    private long lastTrackId = 0;
    private int trackColor;
    private PathLayer polyline;
    private MarkerItem endMarker = null;
    private StyleColorCreator colorCreator = null;
    private GeoPoint startPos;
    private GeoPoint endPos;
    private boolean fullscreenMode = false;
    private MovementDirection movementDirection = new MovementDirection();
    private MapMode mapMode;
    private double mapPosScale;
    private OpenTracksContentObserver contentObserver;
    private Uri tracksUri;
    private Uri trackPointsUri;
    private Uri waypointsUri;
    private int strokeWidth;
    private int protocolVersion = 1;
    private TrackPointsDebug trackPointsDebug;

    //Dummy data for runs
    private List<Run> runs = new ArrayList<>();
    //Dummy data for chairlifts
    private List<Chairlift> chairLifts = new ArrayList<>();

    private List<Segment> segments = new ArrayList<>();


    private TableLayout previousSelectedSegmentView = null;

    private TrackPoint pastTrackPoint = null;

    private int trackPointCounter = 0;

    private ScrollView segmentLayout;

    private ScrollView runChairliftLayout;

    private RecyclerView segmentTable;

    private RecyclerView runsChairliftsTable;

    private SegmentAdapter adapter;

    private RunAdapter runAdapter;

    private ChairliftAdapter liftAdapter;

    private Float scale;

    private MapView mapView;
    private MapPosition currentMapPosition;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int REQUEST_LOCATION_PERMISSION = 1001;


    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toast.makeText(this,"This screen",Toast.LENGTH_SHORT).show();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        strokeWidth = PreferencesUtils.getStrokeWidth();
        mapMode = PreferencesUtils.getMapMode();

        map = binding.map.mapView.map();
        mapPreferences = new MapPreferences(MapsActivity.class.getName(), this);


        setSupportActionBar(binding.toolbar.mapsToolbar);

        createMapViews();
        createLayers();
        //map.getMapPosition().setZoomLevel(MAP_DEFAULT_ZOOM_LEVEL);


        //zooming in and out of maps
        mapView = findViewById(R.id.mapView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //currentMapPosition=mapView.map().setMapPosition();
        currentMapPosition = mapView.map().getMapPosition();
        currentMapPosition.setZoomLevel(100000);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateMapPosition(location.getLatitude(), location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        //Set the button click thing zoomInButton in the map.xml document
        binding.map.zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });

        binding.map.zoomOutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View V){
                zoomOut();
            }
        });
        //Check location permissions and request location updates
        checkLocationPermissionAndRequestUpdate();

        //--------------------------------
        binding.map.fullscreenButton.setOnClickListener(v -> switchFullscreen());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            public void handleOnBackPressed() {
                navigateUp();
            }
        });

        // Get the intent that started this activity
        var intent = getIntent();
        if (intent != null) {
            onNewIntent(intent);
        }
        mapPosScale = map.getMapPosition().getScale();
        // setting initial table to runs and chairlifts table

        segmentLayout = findViewById(R.id.segments_layout);
        runChairliftLayout = findViewById(R.id.runs_chairlifts_layout);

        addRunsAndChairliftsInfo();
        addSegmentInfo();

    }

    private void zoomIn() {
        int currentZoomLevel = currentMapPosition.getZoomLevel();
        int newZoomLevel = currentMapPosition.zoomLevel + 80000;
        currentMapPosition.setZoomLevel(newZoomLevel);
        mapView.map().setMapPosition(currentMapPosition);
    }

    private void zoomOut(){
        int currentZoomLevel=currentMapPosition.getZoomLevel();
        int newZoomLevel= currentMapPosition.zoomLevel-50000;
        currentMapPosition.setZoomLevel(newZoomLevel);
        mapView.map().setMapPosition(currentMapPosition);
    }

    private void checkLocationPermissionAndRequestUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if they have not been granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // If permissions have been granted, start requesting location updates
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        // Request a location update
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {


            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void updateMapPosition(double latitude, double longitude) {
        GeoPoint userLocation = new GeoPoint(latitude, longitude);
        mapView.map().setMapPosition(latitude, longitude, currentMapPosition.zoomLevel);
    }

    private ArrayList<String> usersList = new ArrayList<String>();
    private int selectedUserPosition = 1;

    private void changeUser()
    {
        if (selectedUserPosition == usersList.size() - 1)
        {
            selectedUserPosition = 1;
        }
        showUserOnMap(selectedUserPosition);
        selectedUserPosition = selectedUserPosition + 1;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run()
            {
                removePreviousUser(selectedUserPosition - 1);
                changeUser();
            }
        }, 5000);
    }
    private void removePreviousUser(int i) {
    }
    private void showUserOnMap(int selectedUserPosition) {
        Log.e(TAG, "User "+selectedUserPosition+" will be shown on map now" );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User grants location permissions and begins requesting location updates
                requestLocationUpdates();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("You have denied location permissions and chosen not to be prompted again. To use this feature, go to Settings to enable location permissions.")
                        .show();
            }
        }
    }


    private void switchFullscreen() {
        showFullscreen(!fullscreenMode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resetMapData();

        if (APIConstants.ACTION_DASHBOARD.equals(intent.getAction())) {
            ArrayList<Uri> uris = intent.getParcelableArrayListExtra(APIConstants.ACTION_DASHBOARD_PAYLOAD);
            protocolVersion = intent.getIntExtra(EXTRAS_PROTOCOL_VERSION, 1);
            tracksUri = APIConstants.getTracksUri(uris);
            trackPointsUri = APIConstants.getTrackPointsUri(uris);
            waypointsUri = APIConstants.getWaypointsUri(uris);
            keepScreenOn(intent.getBooleanExtra(EXTRAS_SHOULD_KEEP_SCREEN_ON, false));
            showOnLockScreen(intent.getBooleanExtra(EXTRAS_SHOW_WHEN_LOCKED, false));
            showFullscreen(intent.getBooleanExtra(EXTRAS_SHOW_FULLSCREEN, false));
            isOpenTracksRecordingThisTrack = intent.getBooleanExtra(EXTRAS_OPENTRACKS_IS_RECORDING_THIS_TRACK, false);

            //Dummy data
            for (int i = 1; i <= 4; i++) {
                try {
                    JSONObject c = new JSONObject(getIntent().getStringExtra("c"+i));
                    chairLifts.add(new Chairlift(c.getString("name"),
                            Integer.parseInt(c.getString("distance")),
                            Long.parseLong(c.getString("wtime")),
                            Double.parseDouble(c.getString("speed"))));
                    JSONObject r = new JSONObject(getIntent().getStringExtra("r"+i));
                    runs.add(new Run(r.getString("name"),
                            Double.parseDouble(r.getString("speed")),
                            Double.parseDouble(r.getString("distance")),
                            Integer.parseInt(r.getString("duration")),
                            Double.parseDouble(r.getString("maxSpeed"))));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            readTrackpoints(trackPointsUri, false, protocolVersion);
            readTracks(tracksUri);
            readWaypoints(waypointsUri);

            // navigation Buttons
            LinearLayout containerLayout = findViewById(R.id.container_layout);
            Button runsChairLiftsButton = findViewById(R.id.runsChairLiftsButton);
            Button segmentsButton = findViewById(R.id.segmentsButton);

            // TODO: Fix slow performance issue

            // button Listeners
            runsChairLiftsButton.setOnClickListener(v -> {
                        //inflateLayout(R.layout.table_runs_chairlifts_taken);
                        //inflateLayout(R.layout.table_runs_chairlifts_taken);
                    runChairliftLayout.setVisibility(View.VISIBLE);
                    segmentLayout.setVisibility(View.GONE);
                    updateMapPositionAndRotation(boundingBox.getCenterPoint());
                    for (var layer : map.layers()) {
                        for (int i = 0; i < map.layers().size(); i++) {
                            if (map.layers().get(i) instanceof ItemizedLayer) {
                                if (!waypointsLayer.equals(map.layers().get(i))) {
                                    map.layers().remove(i);
                                }
                            }
                        }
                    }
            });

            segmentsButton.setOnClickListener(v -> {
                //inflateLayout(R.layout.table_segments);
                runChairliftLayout.setVisibility(View.GONE);
                segmentLayout.setVisibility(View.VISIBLE);
            });

        } else if ("geo".equals(intent.getScheme())) {
            Waypoint.fromGeoUri(intent.getData().toString()).ifPresent(waypoint -> {
                final MarkerItem marker = MapUtils.createTappableMarker(this, waypoint);
                waypointsLayer.addItem(marker);
                map.getMapPosition().setPosition(waypoint.getLatLong());
                map.getMapPosition().setZoomLevel(map.viewport().getMaxZoomLevel());
            });
        }
    }


    private class OpenTracksContentObserver extends ContentObserver {

        private final Uri tracksUri;
        private final Uri trackpointsUri;
        private final Uri waypointsUri;
        private final int protocolVersion;

        public OpenTracksContentObserver(Uri tracksUri, Uri trackpointsUri, Uri waypointsUri, int protocolVersion) {
            super(new Handler());
            this.tracksUri = tracksUri;
            this.trackpointsUri = trackpointsUri;
            this.waypointsUri = waypointsUri;
            this.protocolVersion = protocolVersion;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri == null) {
                return; // nothing can be done without an uri
            }
            if (tracksUri.toString().startsWith(uri.toString())) {
                readTracks(tracksUri);
            } else if (trackpointsUri.toString().startsWith(uri.toString())) {
                readTrackpoints(trackpointsUri, true, protocolVersion);
            } else if (waypointsUri.toString().startsWith(uri.toString())) {
                readWaypoints(waypointsUri);
            }
        }
    }

    private void showFullscreen(boolean showFullscreen) {
        this.fullscreenMode = showFullscreen;
        var decorView = getWindow().getDecorView();
        int uiOptions = decorView.getSystemUiVisibility();
        if (showFullscreen) {
            uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            binding.map.fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_fullscreen_exit_48));
        } else {
            uiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE;
            uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            binding.map.fullscreenButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_fullscreen_48));
        }
        binding.toolbar.mapsToolbar.setVisibility(showFullscreen ? View.GONE : View.VISIBLE);
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void navigateUp() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                && isOpenTracksRecordingThisTrack
                && PreferencesUtils.isPipEnabled()) {
            enterPictureInPictureMode();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu, true);
        // TODO: menu.findItem(R.id.share).setVisible(true);
        return true;
    }

    /**
     * Template method to create the map views.
     */
    protected void createMapViews() {
        binding.map.mapView.setClickable(true);
    }


    protected ThemeFile getRenderTheme() {
        Uri mapTheme = PreferencesUtils.getMapThemeUri();
        int winterThemeResourceId = getResources().getIdentifier("elevate_winter", "xml", getPackageName());
        Uri winterThemeUri = Uri.parse("android.resource://" + getPackageName() + "/" + winterThemeResourceId);
        if (mapTheme == null) {
            return VtmThemes.DEFAULT;
        }

        else if(mapTheme.equals(winterThemeUri)){
            //Log.d(TAG, String.valueOf(mapTheme));
            //Log.d(TAG, "Winter Enter MODE");
            //Log.d(TAG, String.valueOf(CustomThemes.WINTER));
            return new StreamRenderTheme("", AssetAdapter.readFileAsStream("elevate_winter.xml"));

        }

        try {
            var renderThemeFile = DocumentFile.fromSingleUri(getApplication(), mapTheme);
            assert renderThemeFile != null;
            var themeFileUri = renderThemeFile.getUri();
            if (Objects.requireNonNull(renderThemeFile.getName(), "Theme files must have a name").endsWith(".zip")) {
                var fragment = themeFileUri.getFragment();
                if (fragment != null) {
                    themeFileUri = themeFileUri.buildUpon().fragment(null).build();
                } else {
                    throw new RuntimeException("Fragment missing, which indicates the theme inside the zip file");
                }
                return new ZipRenderTheme(fragment, new ZipXmlThemeResourceProvider(new ZipInputStream(new BufferedInputStream(getContentResolver().openInputStream(themeFileUri)))));
            }
            return new StreamRenderTheme("/assets/", getContentResolver().openInputStream(themeFileUri));
        } catch (Exception e) {
            Log.e(TAG, "Error loading theme " + mapTheme, e);
            return VtmThemes.DEFAULT;
        }
    }

    protected MultiMapFileTileSource getMapFile() {
        MultiMapFileTileSource tileSource = new MultiMapFileTileSource();
        Set<Uri> mapFiles = PreferencesUtils.getMapUris();
        if (mapFiles.isEmpty()) {
            return null;
        }
        var mapsCount = new AtomicInteger(0);
        mapFiles.stream()
                .filter(uri -> DocumentFile.isDocumentUri(this, uri))
                .map(uri -> DocumentFile.fromSingleUri(this, uri))
                .filter(documentFile -> documentFile != null && documentFile.canRead())
                .forEach(documentFile -> readMapFile(tileSource, mapsCount, documentFile));

        if (mapsCount.get() == 0 && !mapFiles.isEmpty()) {
            Toast.makeText(this, R.string.error_loading_offline_map, Toast.LENGTH_LONG).show();
        }

        return mapsCount.get() > 0 ? tileSource : null;
    }

    private void readMapFile(MultiMapFileTileSource mapDataStore, AtomicInteger mapsCount, DocumentFile documentFile) {
        try {
            var inputStream = (FileInputStream) getContentResolver().openInputStream(documentFile.getUri());
            MapFileTileSource tileSource = new MapFileTileSource();
            tileSource.setMapFileInputStream(inputStream);
            mapDataStore.add(tileSource);
            mapsCount.getAndIncrement();
        } catch (Exception e) {
            Log.e(TAG, "Can't open mapFile", e);
        }
    }

    protected void loadTheme() {
        if (renderTheme != null) {
            renderTheme.dispose();
        }
        renderTheme = map.setTheme(VtmThemes.DEFAULT);

    }

    protected void createLayers() {
        var mapFile = getMapFile();

        if (mapFile != null) {
            VectorTileLayer tileLayer = map.setBaseMap(mapFile);
            loadTheme();

            map.layers().add(new BuildingLayer(map, tileLayer));
            map.layers().add(new LabelLayer(map, tileLayer));

            DefaultMapScaleBar mapScaleBar = new DefaultMapScaleBar(map);
            mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
            mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
            mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
            mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(map, mapScaleBar);
            BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
            renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
            renderer.setOffset(5 * CanvasAdapter.getScale(), 0);
            map.layers().add(mapScaleBarLayer);
            renderTheme = map.setTheme(getRenderTheme());

        } else if (BuildConfig.offline) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_logo_color_24dp)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.no_map_configured)
                    .setPositiveButton(android.R.string.ok, null)
                    .create().show();
        } else if (PreferencesUtils.getOnlineMapConsent()) {
            setOnlineTileLayer();
        } else {
            showOnlineMapConsent();
        }
    }

    private void setOnlineTileLayer() {
        // TODO: Debug overpass source and why its layers aren't visible
        var tileSource = OverpassTileSource.builder()
                /*
                .zoomMin(17)
                .zoomMax(15)
                 */
                .build();

        var bitmapTileSource = DefaultSources.OPENSTREETMAP
                /*
                // TODO: uncomment the following when vector layer is visible:
                // credit to vtm-android-example for specific fade steps and zoom levels
                .zoomMax(15)
                .fadeSteps(new BitmapTileLayer.FadeStep[]{
                        new BitmapTileLayer.FadeStep(15, 16, 1f, 0f),
                        new BitmapTileLayer.FadeStep(16, Viewport.MAX_ZOOM_LEVEL, 0f, 0f)
                })
                */
                .build();
        var builder = new OkHttpClient.Builder();

        // Cache http information
        var cacheDirectory = new File(getExternalCacheDir(), "tiles");
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        var cache = new Cache(cacheDirectory, cacheSize);
        builder.cache(cache);

        var okHttpEngine = new OkHttpEngine.OkHttpFactory(builder);
        bitmapTileSource.setHttpEngine(okHttpEngine);
        bitmapTileSource.setHttpRequestHeaders(Collections.singletonMap("User-Agent", getString(R.string.app_name) + ":" + BuildConfig.APPLICATION_ID));
        VectorTileLayer vectorTileLayer = map.setBaseMap(tileSource);
        BitmapTileLayer bitmapLayer = new BitmapTileLayer(map, bitmapTileSource);
        S3DBLayer databaseLayer = new S3DBLayer(map, vectorTileLayer);
        LabelLayer labelLayer = new LabelLayer(map, vectorTileLayer);
        map.layers().add(bitmapLayer);
        map.layers().add(databaseLayer);
        map.layers().add(labelLayer);

    }

    private void showOnlineMapConsent() {
        var message = new SpannableString(getString(R.string.online_map_consent));
        Linkify.addLinks(message, Linkify.ALL);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_logo_color_24dp)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                    PreferencesUtils.setOnlineMapConsent(true);
                    MapsActivity.this.recreate();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.show();
        ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message),
                "An AlertDialog must have a TextView with id.message"))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Android Activity life cycle method.
     */
    @Override
    protected void onDestroy() {
        binding.map.mapView.onDestroy();
        super.onDestroy();

        if(locationManager!=null &&locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.map_info) {
            var intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.share) {
            sharePicture();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sharePicture() {
        // prepare rendering
        var view = binding.map.mainView;
        glSurfaceView = binding.map.mapView;

        binding.map.sharePictureTitle.setText(R.string.share_picture_title);
        binding.map.controls.setVisibility(View.INVISIBLE);
        binding.map.attribution.setVisibility(View.INVISIBLE);

        // draw
        var canvas = new Canvas();
        var toBeCropped = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //canvas.setBitmap(toBeCropped);

        captureBitmap(canvas::setBitmap);
        view.draw(canvas);

        var bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inTargetDensity = 1;
        toBeCropped.setDensity(Bitmap.DENSITY_NONE);

        int cropFromTop = (int) (70 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        int fromHere = toBeCropped.getHeight() - cropFromTop;
        var croppedBitmap = Bitmap.createBitmap(toBeCropped, 0, cropFromTop, toBeCropped.getWidth(), fromHere);

        try {
            var sharedFolderPath = new File(this.getCacheDir(), "shared");
            sharedFolderPath.mkdir();
            var file = new File(sharedFolderPath, System.currentTimeMillis() + ".png");
            var out = new FileOutputStream(file);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            var share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file));
            share.setType("image/png");
            startActivity(Intent.createChooser(share, "send"));
        } catch (Exception exception) {
            Log.e(TAG, "Error sharing Bitmap", exception);
        }

        binding.map.controls.setVisibility(View.VISIBLE);
        binding.map.attribution.setVisibility(View.VISIBLE);
        binding.map.sharePictureTitle.setText("");
    }

    private GLSurfaceView glSurfaceView;
    private Bitmap snapshotBitmap;

    private interface BitmapReadyCallbacks {
        void onBitmapReady(Bitmap bitmap);
    }

    private void captureBitmap(final BitmapReadyCallbacks bitmapReadyCallbacks) {
        glSurfaceView.queueEvent(() -> {
            EGL10 egl = (EGL10) EGLContext.getEGL();
            GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
            snapshotBitmap = createBitmapFromGLSurface(0, 0, glSurfaceView.getWidth(), glSurfaceView.getHeight(), gl);
            runOnUiThread(() -> bitmapReadyCallbacks.onBitmapReady(snapshotBitmap));
        });

    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {
        int[] bitmapBuffer = new int[w * h];
        int[] bitmapSource = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            Log.e(TAG, "createBitmapFromGLSurface: " + e.getMessage(), e);
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    private void readTrackpoints(Uri data, boolean update, int protocolVersion) {
        Log.i(TAG, "Loading trackpoints from " + data);

        synchronized (map.layers()) {
            var showPauseMarkers = PreferencesUtils.isShowPauseMarkers();
            var latLongs = new ArrayList<GeoPoint>();
            int tolerance = PreferencesUtils.getTrackSmoothingTolerance();

            try {
                // run logic
                getRuns();

                //  chairlift logic
                getChairlifts();

                var trackpointsBySegments = TrackPoint.readTrackPointsBySegments(getContentResolver(), data, lastTrackPointId, protocolVersion);
                if (trackpointsBySegments.isEmpty()) {
                    Log.d(TAG, "No new trackpoints received");
                    return;
                }

                double average = trackpointsBySegments.calcAverageSpeed();
                double maxSpeed = trackpointsBySegments.calcMaxSpeed();
                double averageToMaxSpeed = maxSpeed - average;
                var trackColorMode = PreferencesUtils.getTrackColorMode();
                if (isOpenTracksRecordingThisTrack && !trackColorMode.isSupportsLiveTrack()) {
                    trackColorMode = TrackColorMode.DEFAULT;
                }

                for (var trackPoints : trackpointsBySegments.segments()) {
                    if (!update) {
                        polyline = null; // cut polyline on new segment
                        if (tolerance > 0) { // smooth track
                            trackPoints = MapUtils.decimate(tolerance, trackPoints);
                        }
                    }
                    for (var trackPoint : trackPoints) {
                        lastTrackPointId = trackPoint.getTrackPointId();

                        if (trackPoint.getTrackId() != lastTrackId) {
                            if (trackColorMode == TrackColorMode.BY_TRACK) {
                                trackColor = colorCreator.nextColor();
                            }
                            lastTrackId = trackPoint.getTrackId();
                            polyline = null; // reset current polyline when trackId changes
                            startPos = null;
                            endPos = null;
                        }

                        if (trackColorMode == TrackColorMode.BY_SPEED) {
                            trackColor = MapUtils.getTrackColorBySpeed(average, averageToMaxSpeed, trackPoint);
                            polyline = addNewPolyline(trackColor);
                            if (endPos != null) {
                                polyline.addPoint(endPos);
                            } else if (startPos != null) {
                                polyline.addPoint(startPos);
                            }
                        } else {
                            if (polyline == null) {
                                Log.d(TAG, "Continue new segment.");
                                polyline = addNewPolyline(trackColor);
                            }
                        }

                        // logic to store segment informations and get the real data
                        if (pastTrackPoint == null) {
                            pastTrackPoint = trackPoint;
                            trackPointCounter++;
                        }
                        else {
                            double speed = trackPoint.getSpeed();
                            long time = Math.abs(trackPoint.getTimeMillis()/1000 - pastTrackPoint.getTimeMillis()/1000);
                            double slope = (pastTrackPoint.getLatLong().getLatitude() - trackPoint.getLatLong().getLatitude()) /
                                           (pastTrackPoint.getLatLong().getLongitude() - trackPoint.getLatLong().getLongitude());


                            segments.add(new Segment("Segment " + trackPointCounter, speed, time, slope, pastTrackPoint.getLatLong(), trackPoint.getLatLong()));
                            pastTrackPoint = trackPoint;
                            trackPointCounter++;

                        }

                        endPos = trackPoint.getLatLong();
                        polyline.addPoint(endPos);
                        movementDirection.updatePos(endPos);

                        if (trackPoint.isPause() && showPauseMarkers) {
                            var marker = MapUtils.createPauseMarker(this, trackPoint.getLatLong());
                            waypointsLayer.addItem(marker);
                        }

                        if (!update) {
                            latLongs.add(endPos);
                        }

                        if (startPos == null) {
                            startPos = endPos;
                        }
                    }
                    trackpointsBySegments.debug().trackpointsDrawn += trackPoints.size();
                }
                trackPointsDebug.add(trackpointsBySegments.debug());
            } catch (SecurityException e) {
                Toast.makeText(MapsActivity.this, getString(R.string.error_reading_trackpoints, e.getMessage()), Toast.LENGTH_LONG).show();
                return;
            } catch (Exception e) {
                throw new RuntimeException("Error reading trackpoints", e);
            }

            Log.d(TAG, "Last trackpointId=" + lastTrackPointId);

            if (endPos != null) {
                setEndMarker(endPos);
            }

            GeoPoint myPos = null;
            if (update && endPos != null) {
                myPos = endPos;
                map.render();
            } else if (!latLongs.isEmpty()) {
                boundingBox = new BoundingBox(latLongs).extendMargin(1.2f);
                myPos = boundingBox.getCenterPoint();
            }

            if (myPos != null) {
                updateMapPositionAndRotation(myPos);
            }
            updateDebugTrackPoints();
        }
    }

    // Get info from Group #7
    private void getRuns() {
        Run r1 = new Run("La Plagne",18, 1644, 2000, 30.25);
        Run r2 = new Run("Jay",26, 931, 1100, 40.7);
        Run r3 = new Run("Grand Élan",23, 1051, 1200, 27.67);
        Run r4 = new Run("Tom Barbeau",30, 658, 700, 42.6);
        runs.add(r1);
        runs.add(r2);
        runs.add(r3);
        runs.add(r4);
    }

    // Get info from Group #7
    private void getChairlifts() {
        Chairlift c1 = new Chairlift("L'Étoile", 722, 600, 5.08);
        Chairlift c2 = new Chairlift("Flèche d’Argent",841, 900, 9.08);
        Chairlift c3 = new Chairlift("L'Express",672, 700, 12.08);
        Chairlift c4 = new Chairlift("Le Piedmont",755, 600, 64.08);
        chairLifts.add(c1);
        chairLifts.add(c2);
        chairLifts.add(c3);
        chairLifts.add(c4);
    }

    private void addSegmentInfo() {
        segmentTable = findViewById(R.id.segmentsTableView);
        adapter = new SegmentAdapter(segments, binding.map.mapView);
        segmentLayout.setVisibility(View.GONE);
        segmentTable.setLayoutManager(new LinearLayoutManager(this));
        segmentTable.setAdapter(adapter);
    }

    private void addRunsAndChairliftsInfo() {
        runsChairliftsTable = findViewById(R.id.runsChairliftsTableView);
        runAdapter = new RunAdapter(runs, binding.map.mapView);
        liftAdapter = new ChairliftAdapter(chairLifts, binding.map.mapView);
        ConcatAdapter runAndLiftAdapter = new ConcatAdapter(runAdapter, liftAdapter);
        runChairliftLayout.setVisibility(View.GONE);
        runsChairliftsTable.setLayoutManager(new LinearLayoutManager(this));
        runsChairliftsTable.setAdapter(runAndLiftAdapter);
    }

    private void resetMapData() {
        unregisterContentObserver();

        tracksUri = null;
        trackPointsUri = null;
        waypointsUri = null;

        var layers = map.layers();

        // polylines
        if (polylinesLayer != null) {
            layers.remove(polylinesLayer);
        }
        polylinesLayer = new GroupLayer(map);
        layers.add(polylinesLayer);

        // tracks
        lastTrackId = 0;
        lastTrackPointId = 0;
        colorCreator = new StyleColorCreator(StyleColorCreator.GOLDEN_RATIO_CONJUGATE / 2);
        trackColor = colorCreator.nextColor();
        polyline = null;
        startPos = null;
        endPos = null;
        endMarker = null;
        boundingBox = null;
        movementDirection = new MovementDirection();
        trackPointsDebug = new TrackPointsDebug();

        // waypoints
        if (waypointsLayer != null) {
            layers.remove(waypointsLayer);
        }
        waypointsLayer = createWaypointsLayer();
        map.layers().add(waypointsLayer);
        lastWaypointId = 0;
    }

    public void updateDebugTrackPoints() {
        if (PreferencesUtils.isDebugTrackPoints()) {
            binding.map.trackpointsDebugInfo.setText(
                    getString(R.string.debug_trackpoints_info,
                            trackPointsDebug.trackpointsReceived,
                            trackPointsDebug.trackpointsInvalid,
                            trackPointsDebug.trackpointsDrawn,
                            trackPointsDebug.trackpointsPause,
                            trackPointsDebug.segments,
                            protocolVersion
                    ));
        } else {
            binding.map.trackpointsDebugInfo.setText("");
        }
    }//[lat=45.88163,lon=-74.160012]
    private View infoWindow;
    private void setEndMarker(GeoPoint endPos) {
        synchronized (map.layers()) {
            if (endMarker != null) {
                endMarker.geoPoint = endPos;
                endMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                waypointsLayer.populate();
                map.render();
            } else {


                for(int i=0;i<5;i++){//45.88163, -74.160012
                    if(i ==0){
                        MarkerItem dumMarker = new MarkerItem(endPos.toString() + i, "", new GeoPoint(45.88153, -74.159912));
                        var symNew = MapUtils.createMarkerSymbol(this, R.drawable.ic_marker_orange_pushpin_modern, false, MarkerSymbol.HotspotPlace.CENTER);
                        dumMarker.setMarker(symNew);
                        //dumMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                        waypointsLayer.addItem(dumMarker);
                    }else if(i==1){
                        MarkerItem dumMarker = new MarkerItem(endPos.toString() + i, "", new GeoPoint(45.88173, -74.160112));
                        var symNew = MapUtils.createMarkerSymbol(this, R.drawable.ic_marker_orange_pushpin_modern, false, MarkerSymbol.HotspotPlace.CENTER);
                        dumMarker.setMarker(symNew);
                        //dumMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                        waypointsLayer.addItem(dumMarker);
                    }else if(i==2){
                        MarkerItem dumMarker = new MarkerItem(endPos.toString() + i, "", new GeoPoint(45.88163, -74.159912));
                        var symNew = MapUtils.createMarkerSymbol(this, R.drawable.ic_marker_common_green, false, MarkerSymbol.HotspotPlace.CENTER);
                        dumMarker.setMarker(symNew);
                        //dumMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                        waypointsLayer.addItem(dumMarker);
                    }else if(i==3){
                        MarkerItem dumMarker = new MarkerItem(endPos.toString() + i, "", new GeoPoint(45.88163, -74.159912));
                        var symNew = MapUtils.createMarkerSymbol(this, R.drawable.ic_marker_common_green, false, MarkerSymbol.HotspotPlace.CENTER);
                        dumMarker.setMarker(symNew);
                        //dumMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                        waypointsLayer.addItem(dumMarker);
                    }else {
                        MarkerItem dumMarker = new MarkerItem(endPos.toString() + i, "", new GeoPoint(45.88163, -74.159912));
                        var symNew = MapUtils.createMarkerSymbol(this, R.drawable.ic_marker_common_green, false, MarkerSymbol.HotspotPlace.CENTER);
                        dumMarker.setMarker(symNew);
                        //dumMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                        waypointsLayer.addItem(dumMarker);

                    }

                }

                endMarker = new MarkerItem(endPos.toString(), "", endPos);
                var symbol = MapUtils.createMarkerSymbol(this, R.drawable.ic_compass, false, MarkerSymbol.HotspotPlace.CENTER);
                endMarker.setMarker(symbol);
                endMarker.setRotation(MapUtils.rotateWith(mapMode, movementDirection));
                waypointsLayer.addItem(endMarker);

                waypointsLayer.setOnItemGestureListener(new ItemizedLayer.OnItemGestureListener<MarkerInterface>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, MarkerInterface item) {

                        //1,5,0
                        if(index==2){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            builder.setTitle("Users at Same location");
                            builder.setMessage("3 Users are at same Location\n\n" +
                                    "UserID: 2, UserName: Dummy User 2\n" +
                                    "UserID: 3, UserName: Dummy User 3\n" +
                                    "UserID: 4, UserName: Dummy User 4\n");

                            // Open app settings when positive button is clicked
                            builder.setPositiveButton("Ok", (dialog, which) -> {
                                dialog.dismiss();
                            });

                            // Close the app when negative button is clicked
                            builder.setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            });

                            builder.show();
                        }


                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, MarkerInterface item) {
                        return false;
                    }
                });



            }
        }
    }

    private PathLayer addNewPolyline(int trackColor) {
        polyline = new PathLayer(map, trackColor, strokeWidth);
        polylinesLayer.layers.add(polyline);
        return this.polyline;
    }


    private void readWaypoints(Uri data) {
        Log.i(TAG, "Loading waypoints from " + data);

        try {
            for (var waypoint : Waypoint.readWaypoints(getContentResolver(), data, lastWaypointId)) {
                lastWaypointId = waypoint.getId();
                final MarkerItem marker = MapUtils.createTappableMarker(this, waypoint);
                waypointsLayer.addItem(marker);
            }
        } catch (SecurityException e) {
            Log.w(TAG, "No permission to read waypoints");
        } catch (Exception e) {
            Log.e(TAG, "Reading waypoints failed", e);
        }
    }

    private ItemizedLayer createWaypointsLayer() {
        var symbol = MapUtils.createPushpinSymbol(this);
        return new ItemizedLayer(map, new ArrayList<>(), symbol, this);
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerInterface item) {
        MarkerItem markerItem = (MarkerItem) item;
        if (markerItem.uid != null) {
            var intent = new Intent("de.dennisguse.opentracks.MarkerDetails");
            intent.putExtra(EXTRA_MARKER_ID, (Long) markerItem.getUid());
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerInterface item) {
        return false;
    }

    private void readTracks(Uri data) {
        var tracks = Track.readTracks(getContentResolver(), data);
        if (!tracks.isEmpty()) {
            var statistics = new TrackStatistics(tracks);
            removeStatisticElements();
            PreferencesUtils.getStatisticElements()
                    .stream()
                    .sorted(comparingInt(StatisticElement::ordinal))
                    .forEach(se -> addStatisticElement(se.getText(this, statistics)));
        }
    }

    private void removeStatisticElements() {
        var childsToRemove = new ArrayList<View>();
        for (int i = 0; i < binding.map.statisticsLayout.getChildCount(); i++) {
            var childView = binding.map.statisticsLayout.getChildAt(i);
            if (childView instanceof TextView) {
                childsToRemove.add(childView);
            }
        }
        childsToRemove.forEach((view -> {
            binding.map.statisticsLayout.removeView(view);
            binding.map.statistics.removeView(view);
        }));
    }

    private void addStatisticElement(String text) {
        var textView = new TextView(this);
        textView.setId(View.generateViewId());
        textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        textView.setTextColor(getColor(R.color.track_statistic));
        textView.setTextSize(COMPLEX_UNIT_PT, 10);
        binding.map.statisticsLayout.addView(textView);
        binding.map.statistics.addView(textView);
    }


    @Override
    public void onResume() {
        super.onResume();

        mapPreferences.load(map);
        binding.map.mapView.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && boundingBox != null) {
            var mapPos = map.getMapPosition();
            mapPos.setByBoundingBox(boundingBox, map.getWidth(), map.getHeight());
            mapPos.setBearing(mapMode.getHeading(movementDirection));
            map.animator().animateTo(mapPos);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        int visibility = isInPictureInPictureMode ? View.GONE : View.VISIBLE;
        binding.toolbar.mapsToolbar.setVisibility(visibility);
        binding.map.fullscreenButton.setVisibility(visibility);
        binding.map.statistics.setVisibility(visibility);
    }

    private boolean isPiPMode() {
        return isInPictureInPictureMode();
    }

    @Override
    protected void onPause() {
        if (!isPiPMode()) {
            mapPreferences.save(map);
            binding.map.mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "register content observer");
        if (tracksUri != null && trackPointsUri != null && waypointsUri != null) {
            contentObserver = new OpenTracksContentObserver(tracksUri, trackPointsUri, waypointsUri, protocolVersion);
            try {
                getContentResolver().registerContentObserver(tracksUri, false, contentObserver);
                getContentResolver().registerContentObserver(trackPointsUri, false, contentObserver);
                if (waypointsUri != null) {
                    getContentResolver().registerContentObserver(waypointsUri, false, contentObserver);
                }
            } catch (SecurityException se) {
                Log.e(TAG, "Error on registering OpenTracksContentObserver", se);
                Toast.makeText(this, R.string.error_reg_content_observer, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        unregisterContentObserver();
        super.onStop();
    }


    private void unregisterContentObserver() {
        if (contentObserver != null) {
            Log.d(TAG, "unregister content observer");
            getContentResolver().unregisterContentObserver(contentObserver);
            contentObserver = null;
        }
    }

    private void updateMapPositionAndRotation(final GeoPoint myPos) {
        var newPos = map.getMapPosition().setPosition(myPos).setBearing(mapMode.getHeading(movementDirection));
        map.animator().animateTo(newPos);
    }

}
