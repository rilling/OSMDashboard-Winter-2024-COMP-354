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

        private TextView distance;

        private TableLayout chairliftView;

        public ChairliftViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_Name);
            waitingTime = itemView.findViewById(R.id.wTime);
            avgSpeed = itemView.findViewById(R.id.avgSpeedInput);
            distance = itemView.findViewById(R.id.distance);
            chairliftView = itemView.findViewById(R.id.chairLiftView);
        }
    }
}
