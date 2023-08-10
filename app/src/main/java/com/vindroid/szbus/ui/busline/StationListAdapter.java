package com.vindroid.szbus.ui.busline;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.StationActivity;
import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.ViewHolder> {
    private final static String TAG;

    private LinkedList<Station> mStations;
    private List<RunningBus> mRunningBuses;

    static {
        TAG = App.getTag(StationListAdapter.class.getSimpleName());
    }

    public StationListAdapter() {
        mStations = new LinkedList<>();
        mRunningBuses = new ArrayList<>();
    }

    public void updateStation(LinkedList<Station> stations) {
        mStations = new LinkedList<>(stations);
    }

    public void updateRunningBuses(List<RunningBus> runningBuses) {
        mRunningBuses = new ArrayList<>(runningBuses);
    }

    public LinkedList<Station> getStations() {
        return mStations;
    }

    public List<RunningBus> getRunningBuses() {
        return mRunningBuses;
    }

    @NonNull
    @Override
    public StationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_station, parent, false);
        return new StationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationListAdapter.ViewHolder holder, int position) {
        Station station = mStations.get(position);
        String stationId = station.getId();
        holder.stationIndexView.setText(String.valueOf(position));
        holder.stationNameView.setText(station.getName());
        // TODO edit subway ui style
        if (station.getSubways().size() > 0) {
            holder.subwayView.setText(Arrays.toString(station.getSubways().toArray()));
        }

        RunningBus bus = null;
        for (RunningBus value : mRunningBuses) {
            if (stationId.equals(value.getStationId())) {
                bus = value;
                break;
            }
        }
        holder.busIconView.setVisibility(bus != null ? View.VISIBLE : View.INVISIBLE);
        holder.busNameView.setVisibility(bus != null ? View.VISIBLE : View.GONE);
        holder.busComingView.setVisibility(bus != null ? View.VISIBLE : View.GONE);
        if (bus != null) {
            holder.busNameView.setText(bus.getInfo());
            holder.busComingView.setText(bus.getInTime());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.KEY_ID, station.getId());
            intent.putExtra(Constants.KEY_NAME, station.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mStations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView busIconView;
        public TextView stationIndexView;
        public TextView stationNameView;
        public TextView subwayView;
        public TextView busNameView;
        public TextView busComingView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            busIconView = itemView.findViewById(R.id.bus_icon);
            stationIndexView = itemView.findViewById(R.id.station_index);
            stationNameView = itemView.findViewById(R.id.station_name);
            subwayView = itemView.findViewById(R.id.subway);
            busNameView = itemView.findViewById(R.id.bus_name);
            busComingView = itemView.findViewById(R.id.bus_time);
        }
    }
}
