package de.storchp.opentracks.osmplugin.dashboardapi;

import static org.oscim.map.Animator.ANIM_MOVE;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.oscim.core.BoundingBox;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.android.MapView;
import org.oscim.core.GeoPoint;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.Layer;
import org.oscim.layers.PathLayer;
import org.oscim.layers.marker.MarkerInterface;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.utils.animation.Easing;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import de.storchp.opentracks.osmplugin.R;
import de.storchp.opentracks.osmplugin.maps.MovementDirection;
import de.storchp.opentracks.osmplugin.utils.MapMode;
import de.storchp.opentracks.osmplugin.utils.MapUtils;
import de.storchp.opentracks.osmplugin.utils.PreferencesUtils;

public class ChairliftAdapter extends RecyclerView.Adapter<ChairliftAdapter.ChairliftViewHolder> implements ItemizedLayer.OnItemGestureListener<MarkerInterface> {
    private List<Chairlift> chairlifts;
    private DecimalFormat formatter;
    private MapView mapView;
    private TableLayout previousChairliftView;
    private GroupLayer polylinesLayer;
    private MovementDirection movementDirection;
    private ItemizedLayer waypointsLayer;
    private MapMode mapMode;

    public ChairliftAdapter(List<Chairlift> chairlifts, MapView mapView) {
        this.chairlifts = chairlifts;
        this.formatter = new DecimalFormat("#0.00");
        previousChairliftView = null;
        this.mapView = mapView;
        polylinesLayer = new GroupLayer(mapView.map());
        mapMode = PreferencesUtils.getMapMode();
        movementDirection = new MovementDirection();
    }

    @NonNull
    @Override
    public ChairliftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chairlift_item, parent, false);
        return new ChairliftViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChairliftViewHolder holder, int position) {
        Chairlift chairlift = chairlifts.get(position);
        // holder.bind(chairlift); To be implemented.
    }

    @Override
    public int getItemCount() {
        return chairlifts.size();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerInterface item) {
        return false;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerInterface item) {
        return false;
    }

    public class ChairliftViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        private  TextView waitingTime;

        private TextView avgSpeed;

        private TextView elevationGain;

        private TableLayout chairliftView;

        public ChairliftViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_Name);
            waitingTime = itemView.findViewById(R.id.wTime);
            avgSpeed = itemView.findViewById(R.id.avgSpeedInput);
            elevationGain = itemView.findViewById(R.id.distanceInput);
            chairliftView = itemView.findViewById(R.id.chairLiftView);
        }

        //bind method adapted from SegmentAdapter and RunAdapter classes to implement same functionalities but for chairlift class.
        //code adapted from SegmentAdapter and RunAdapter and adapted for chairlifts.
        public void bind(Chairlift chairlift) {
            name.setText(chairlift.getName());
            waitingTime.setText(formatDuration(chairlift.getWaitingTime()));
            avgSpeed.setText(String.format(Locale.getDefault(),"%.2f km/h", chairlift.getAverageSpeed()));
            elevationGain.setText(String.format(Locale.getDefault(), "%.2f meters", chairlift.getElevationGain()));

            chairliftView.setOnClickListener( v -> {
                Toast.makeText(v.getContext(), chairlift.getName() + " Clicked", Toast.LENGTH_SHORT).show();
                chairliftView.setBackgroundColor(Color.rgb(212, 221, 232));

                //Removing previous layer if any
                mapView.map().layers().remove(polylinesLayer);

                for (int i = 0; i < polylinesLayer.map().layers().size(); i++) {
                    Layer layer = polylinesLayer.map().layers().get(i);
                    if (layer instanceof PathLayer) {
                        polylinesLayer.map().layers().remove(i);
                    }
                }

                PathLayer line = new PathLayer(mapView.map(), Color.BLUE, 12);
                //Add points to the PathLayer
                line.addPoint(chairlift.getStartPoint());
                line.addPoint(chairlift.getEndPoint());

                polylinesLayer.map().layers().add(line);
                mapView.map().layers().add(polylinesLayer);

                List<GeoPoint> latLongs = line.getPoints();
                Waypoint startWayPoint = new Waypoint(chairlift.getStartPoint(), "start point");
                Waypoint endWayPoint = new Waypoint(chairlift.getEndPoint(), "end point");

                final MarkerItem startPin = MapUtils.createTappableMarker(mapView.getContext(), startWayPoint);
                final MarkerItem endPin = MapUtils.createTappableMarker(mapView.getContext(), endWayPoint);
                MarkerSymbol greenSymbol = MapUtils.createMarkerSymbol(mapView.getContext(), R.drawable.ic_marker_green_pushpin_modern, false, MarkerSymbol.HotspotPlace.CENTER);
                MarkerSymbol redSymbol = MapUtils.createMarkerSymbol(mapView.getContext(), R.drawable.ic_marker_red_pushpin_modern, false, MarkerSymbol.HotspotPlace.CENTER);
                startPin.setMarker(greenSymbol);
                endPin.setMarker(redSymbol);

                if (waypointsLayer != null) {
                    mapView.map().layers().remove(waypointsLayer);
                }

                waypointsLayer = createWaypointsLayer();
                mapView.map().layers().add(waypointsLayer);
                waypointsLayer.addItem(startPin);
                waypointsLayer.addItem(endPin);

                BoundingBox boundingBox = new BoundingBox(latLongs);
                updateMapPositionAndRotation(boundingBox.getCenterPoint());
                mapView.map().animator().animateTo(500, boundingBox, Easing.Type.LINEAR, ANIM_MOVE);

            });

        }
    }

    //code adapted/taken from RunAdapter and SegmentAdapter classes
    private ItemizedLayer createWaypointsLayer() {
        MarkerSymbol symbol = MapUtils.createPushpinSymbol(mapView.getContext());
        return new ItemizedLayer(mapView.map(), symbol);
    }
    //code adapted/taken from RunAdapter and SegmentAdapter classes
    private void updateMapPositionAndRotation(final GeoPoint myPos){
        mapView.map().getMapPosition().setPosition(myPos).setBearing(mapMode.getHeading(movementDirection));

    }
    //code adapted/taken from RunAdapter class
    private String formatDuration(long durationInSeconds) {
        return DateUtils.formatElapsedTime(durationInSeconds);
    }
}
