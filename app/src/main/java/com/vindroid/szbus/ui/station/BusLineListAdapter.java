package com.vindroid.szbus.ui.station;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusLineActivity;
import com.vindroid.szbus.R;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BusLineListAdapter extends RecyclerView.Adapter<BusLineListAdapter.ViewHolder> {
    private final static String TAG;

    private List<InComingBusLine> mBusLines;

    static {
        TAG = App.getTag(BusLineListAdapter.class.getSimpleName());
    }

    public BusLineListAdapter() {
        mBusLines = new LinkedList<>();
    }

    public void updateBusLines(List<InComingBusLine> busLines) {
        mBusLines = new ArrayList<>(busLines);
    }

    public List<InComingBusLine> getData() {
        return mBusLines;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bus_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InComingBusLine busLine = mBusLines.get(position);
        holder.busLineName.setText(busLine.getName());
        holder.busLineTo.setText(busLine.getEndStationName());
        int coming = busLine.getComing();
        if (coming == InComingBusLine.COMING_ERR) {
            holder.busLineStatus.setText(R.string.coming_err);
            holder.busLineStatus.setTextColor(App.getColorById(R.color.red));
        } else if (coming == InComingBusLine.COMING_NO) {
            holder.busLineStatus.setText(R.string.coming_not);
        } else if (coming == InComingBusLine.COMING_NOW) {
            holder.busLineStatus.setText(R.string.coming_now);
            holder.busLineStatus.setTextColor(App.getColorById(R.color.red));
        } else {
            holder.busLineStatus.setText(String.format(App.getStringById(R.string.coming_still), coming));
        }

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
        return mBusLines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView busLineName;
        public TextView busLineTo;
        public TextView busLineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewStub viewStub = itemView.findViewById(R.id.stub_info);
            viewStub.inflate();
            busLineName = itemView.findViewById(R.id.bus_line_name);
            busLineTo = itemView.findViewById(R.id.bus_line_to);
            busLineStatus = itemView.findViewById(R.id.status);
        }
    }
}
