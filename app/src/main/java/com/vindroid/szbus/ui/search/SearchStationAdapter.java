package com.vindroid.szbus.ui.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.AdapterListener;
import com.vindroid.szbus.databinding.ItemSearchStationBinding;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.ui.station.StationActivity;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchStationAdapter extends RecyclerView.Adapter<SearchStationAdapter.ViewHolder> {
    private List<StationDetail> mStationList;
    private AdapterListener mListener;

    public SearchStationAdapter() {
        mStationList = new ArrayList<>();
    }

    public SearchStationAdapter(AdapterListener listener) {
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
        ItemSearchStationBinding binding = ItemSearchStationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchStationAdapter.ViewHolder holder, int position) {
        final StationDetail station = mStationList.get(position);
        holder.binding.stationName.setText(station.getName());
        holder.binding.stationAddress.setText(station.getAddress());
        holder.binding.stationBusLines.setText(station.getBusLinesText());

        holder.itemView.setOnClickListener(v -> {
            if (mListener == null) {
                Intent intent = new Intent(holder.itemView.getContext(), StationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_ID, station.getId());
                intent.putExtra(Constants.KEY_NAME, station.getName());
                holder.itemView.getContext().startActivity(intent);
            } else {
                mListener.onItemClicked(position, station);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchStationBinding binding;

        public ViewHolder(ItemSearchStationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
