package com.vindroid.szbus.ui.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusLineActivity;
import com.vindroid.szbus.R;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchBusLineAdapter extends RecyclerView.Adapter<SearchBusLineAdapter.ViewHolder> {
    private final static String TAG;
    private List<BusLine> mBusLineList;

    static {
        TAG = App.getTag(SearchBusLineAdapter.class.getSimpleName());
    }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_bus_line, parent, false);
        return new SearchBusLineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusLine busLine = mBusLineList.get(position);
        holder.nameView.setText(busLine.getName());
        holder.fromView.setText(busLine.getStartStationName());
        holder.toView.setText(busLine.getEndStationName());

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
        public TextView nameView;
        public TextView fromView;
        public TextView toView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.bus_name);
            fromView = itemView.findViewById(R.id.bus_from);
            toView = itemView.findViewById(R.id.bus_to);
        }
    }
}
