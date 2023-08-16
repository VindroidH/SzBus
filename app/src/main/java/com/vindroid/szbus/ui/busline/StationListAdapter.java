package com.vindroid.szbus.ui.busline;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vindroid.szbus.databinding.ListItemStationBinding;
import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.ui.station.StationActivity;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.ViewHolder> {
    private LinkedList<Station> mStations;
    private List<RunningBus> mRunningBuses;

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
        ListItemStationBinding binding = ListItemStationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StationListAdapter.ViewHolder holder, int position) {
        Station station = mStations.get(position);
        String stationId = station.getId();
        holder.binding.stationIndex.setText(String.valueOf(position));
        holder.binding.stationName.setText(station.getName());
        // TODO edit subway ui style
        if (station.getSubways().size() > 0) {
            holder.binding.subway.setVisibility(View.VISIBLE);
            holder.binding.subway.setText(Arrays.toString(station.getSubways().toArray()));
        } else {
            holder.binding.subway.setVisibility(View.GONE);
        }

        RunningBus bus = null;
        for (RunningBus value : mRunningBuses) {
            if (stationId.equals(value.getStationId())) {
                bus = value;
                break;
            }
        }
        holder.binding.busIcon.setVisibility(bus != null ? View.VISIBLE : View.INVISIBLE);
        holder.binding.busName.setVisibility(bus != null ? View.VISIBLE : View.GONE);
        holder.binding.busTime.setVisibility(bus != null ? View.VISIBLE : View.GONE);
        if (bus != null) {
            holder.binding.busName.setText(bus.getInfo());
            holder.binding.busTime.setText(bus.getInTime());
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
        ListItemStationBinding binding;

        public ViewHolder(ListItemStationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
