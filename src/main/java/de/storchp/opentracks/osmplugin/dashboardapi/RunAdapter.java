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

import de.storchp.opentracks.osmplugin.R;
import de.storchp.opentracks.osmplugin.maps.MovementDirection;
import de.storchp.opentracks.osmplugin.utils.MapMode;
import de.storchp.opentracks.osmplugin.utils.MapUtils;
import de.storchp.opentracks.osmplugin.utils.PreferencesUtils;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> implements ItemizedLayer.OnItemGestureListener<MarkerInterface> {
    private List<Run> runs;
    private DecimalFormat formatter;
    private MapView mapView;
    private TableLayout previousRunView;
    private GroupLayer polylinesLayer;
    private MovementDirection movementDirection;
    private ItemizedLayer waypointsLayer;
    private MapMode mapMode;

    public RunAdapter(List<Run> runs, MapView mapView) {
        this.runs = runs;
        this.formatter = new DecimalFormat("#0.00");
        previousRunView = null;
        this.mapView = mapView;
        polylinesLayer = new GroupLayer(mapView.map());
        mapMode = PreferencesUtils.getMapMode();
        movementDirection = new MovementDirection();
    }

    @NonNull
    @Override
    public RunAdapter.RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.run_item, parent, false);
        return new RunAdapter.RunViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RunAdapter.RunViewHolder holder, int position) {
        Run run = runs.get(position);
        // holder.bind(run); To be implemented by Farnaz.
    }

    @Override
    public int getItemCount() {
        return runs.size();
    }

    @Override
    public boolean onItemSingleTapUp(int index, MarkerInterface item) {
        return false;
    }

    @Override
    public boolean onItemLongPress(int index, MarkerInterface item) {
        return false;
    }

    public class RunViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView avgSpeed;
        private TextView maxSpeed;
        private TextView runTime;
        private TextView distance;
        private TableLayout runView;
        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_Name);
            avgSpeed = itemView.findViewById(R.id.avgSpeedInput);
            maxSpeed = itemView.findViewById(R.id.maxSpeedInput);
            runTime = itemView.findViewById(R.id.runTimeInput);
            distance = itemView.findViewById(R.id.distanceInput);
            runView = itemView.findViewById(R.id.runView);
        }
    }


}
