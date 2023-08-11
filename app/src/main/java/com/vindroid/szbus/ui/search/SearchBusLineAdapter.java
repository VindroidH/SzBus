package com.vindroid.szbus.ui.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.ui.busline.BusLineActivity;
import com.vindroid.szbus.databinding.ItemSearchBusLineBinding;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchBusLineAdapter extends RecyclerView.Adapter<SearchBusLineAdapter.ViewHolder> {
    private List<BusLine> mBusLineList;

    SearchBusLineAdapter() {
        mBusLineList = new ArrayList<>();
    }

    public void updateData(List<BusLine> busLineList) {
        mBusLineList = new ArrayList<>(busLineList);
    }

    public List<BusLine> getData() {
        return mBusLineList;
    }

    @NonNull
    @Override
    public SearchBusLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchBusLineBinding binding = ItemSearchBusLineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusLine busLine = mBusLineList.get(position);
        holder.binding.busName.setText(busLine.getName());
        holder.binding.busFrom.setText(busLine.getStartStationName());
        holder.binding.busTo.setText(busLine.getEndStationName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BusLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.KEY_ID, busLine.getId());
            intent.putExtra(Constants.KEY_NAME, busLine.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mBusLineList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchBusLineBinding binding;

        public ViewHolder(ItemSearchBusLineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
