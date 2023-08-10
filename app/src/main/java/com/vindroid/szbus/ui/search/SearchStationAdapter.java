package com.vindroid.szbus.ui.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.StationActivity;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchStationAdapter extends RecyclerView.Adapter<SearchStationAdapter.ViewHolder> {
    private final static String TAG;

    private List<StationDetail> mStationList;
    private OnClickListener mListener;

    static {
        TAG = App.getTag(SearchStationAdapter.class.getSimpleName());
    }

    public interface OnClickListener {
        void onItemClick(Object obj);
    }

    public SearchStationAdapter() {
        mStationList = new ArrayList<>();
    }

    public SearchStationAdapter(OnClickListener listener) {
        mStationList = new ArrayList<>();
        mListener = listener;
    }

    public void updateData(List<StationDetail> stationList) {
        mStationList = new ArrayList<>(stationList);
    }

    public List<StationDetail> getData() {
        return mStationList;
    }

    @NonNull
    @Override
    public SearchStationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_station, parent, false);
        return new SearchStationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchStationAdapter.ViewHolder holder, int position) {
        final StationDetail station = mStationList.get(position);
        holder.nameView.setText(station.getName());
        holder.addressView.setText(station.getAddress());
        holder.busLinesView.setText(station.getBusLinesText());

        holder.itemView.setOnClickListener(v -> {
            if (mListener == null) {
                Intent intent = new Intent(holder.itemView.getContext(), StationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_ID, station.getId());
                intent.putExtra(Constants.KEY_NAME, station.getName());
                holder.itemView.getContext().startActivity(intent);
            } else {
                mListener.onItemClick(station);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView addressView;
        public TextView busLinesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.station_name);
            addressView = itemView.findViewById(R.id.station_address);
            busLinesView = itemView.findViewById(R.id.station_bus_lines);
        }
    }
}
