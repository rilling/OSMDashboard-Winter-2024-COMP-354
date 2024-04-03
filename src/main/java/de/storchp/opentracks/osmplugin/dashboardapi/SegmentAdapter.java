package de.storchp.opentracks.osmplugin.dashboardapi;

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

import org.oscim.android.MapView;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.Layer;
import org.oscim.layers.PathLayer;


import java.text.DecimalFormat;
import java.util.List;

import de.storchp.opentracks.osmplugin.R;

public class SegmentAdapter extends RecyclerView.Adapter<SegmentAdapter.SegmentViewHolder> {
    private List<Segment> segments;
    private DecimalFormat formatter;
    private MapView mapView;

    private TableLayout previousSegmentView;

    private GroupLayer polylinesLayer;

    public SegmentAdapter(List<Segment> segments, MapView mapView) {
        this.segments = segments;
        this.formatter = new DecimalFormat("#0.00");
        previousSegmentView = null;
        this.mapView = mapView;
        polylinesLayer = new GroupLayer(mapView.map());
    }

    @NonNull
    @Override
    public SegmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_item, parent, false);
        return new SegmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentViewHolder holder, int position) {
        Segment segment = segments.get(position);
        holder.bind(segment);
    }

    @Override
    public int getItemCount() {
        return segments.size();
    }



    public class SegmentViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView speed;
        private TextView time;
        private TextView slope;
        private TextView distance;
        private TableLayout segmentView;

        public SegmentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_Name);
            speed = itemView.findViewById(R.id.speed);
            time = itemView.findViewById(R.id.time);
            slope = itemView.findViewById(R.id.slope);
            distance = itemView.findViewById(R.id.distance);
            segmentView = itemView.findViewById(R.id.segmentView);

        }

        public void bind(Segment segment) {
            name.setText(segment.getName());
            speed.setText(formatter.format(segment.getSpeed()));
            time.setText(DateUtils.formatElapsedTime(segment.getTime()));
            slope.setText(formatter.format(segment.getSlope()));
            distance.setText(formatter.format(segment.getSpeed() * segment.getTime()));

            segmentView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), segment.getName() + " Clicked", Toast.LENGTH_SHORT).show();
                segmentView.setBackgroundColor(Color.rgb(212, 221, 232));
                if (previousSegmentView != null) previousSegmentView.setBackgroundColor(Color.WHITE);
                previousSegmentView = segmentView;

                // removing the previous layer if any
                mapView.map().layers().remove(polylinesLayer);

                for (int i =  0; i < polylinesLayer.map().layers().size(); i++) {
                    Layer layer = polylinesLayer.map().layers().get(i);
                    if (layer instanceof PathLayer) {
                        polylinesLayer.map().layers().remove(i);
                    }
                }

                PathLayer line = new PathLayer(mapView.map(), Color.BLUE, 12);
                // Add points to the PathLayer
                line.addPoint(segment.getStartPoint());
                line.addPoint(segment.getEndPoint());
                // Add the PathLayer to the MapView
                polylinesLayer.map().layers().add(line);
                mapView.map().layers().add(polylinesLayer);
            });
        }
    }
}
