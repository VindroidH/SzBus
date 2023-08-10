package com.vindroid.szbus.ui.main.favorite;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusLineActivity;
import com.vindroid.szbus.R;
import com.vindroid.szbus.StationActivity;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.utils.Constants;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final static String TAG;

    private LinkedList<Favorite> mFavoriteList;

    static {
        TAG = App.getTag(FavoriteListAdapter.class.getSimpleName());
    }

    public FavoriteListAdapter() {
        mFavoriteList = new LinkedList<>();
    }

    public void updateData(LinkedList<Favorite> favoriteList) {
        try {
            mFavoriteList = new LinkedList<>();
            for (int i = 0; i < favoriteList.size(); i++) {
                mFavoriteList.add((Favorite) favoriteList.get(i).clone());
            }
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "[updateData] cannot deep copy data");
            mFavoriteList = new LinkedList<>(favoriteList);
        }
    }

    public LinkedList<Favorite> getData() {
        return mFavoriteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = null;
        InComingBusLine busLine = null;
        int index = 0;
        for (int i = 0; i < mFavoriteList.size(); i++) {
            for (int j = 0; j < mFavoriteList.get(i).getBusLines().size(); j++) {
                if (index == position) {
                    busLine = mFavoriteList.get(i).getBusLine(j);
                    if (j == 0) {
                        station = mFavoriteList.get(i).getStation();
                    }
                    break;
                }
                index += 1;
            }
            if (busLine != null) {
                break;
            }
        }
        if (busLine == null) busLine = new InComingBusLine();
        if (station != null) {
            holder.stationName.setVisibility(View.VISIBLE);
            holder.stationName.setText(station.getName());
        } else {
            holder.stationName.setVisibility(View.GONE);
        }

        holder.busLineName.setText(busLine.getName());
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

        if (station != null) {
            final Station fStation = station;
            holder.stationName.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_ID, fStation.getId());
                intent.putExtra(Constants.KEY_NAME, fStation.getName());
                intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_FAVORITE);
                v.getContext().startActivity(intent);
            });
        }
        final InComingBusLine fBusLine = busLine;
        holder.busLineRoot.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BusLineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.KEY_ID, fBusLine.getId());
            intent.putExtra(Constants.KEY_NAME, fBusLine.getName());
            intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_FAVORITE);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < mFavoriteList.size(); i++) {
            count += mFavoriteList.get(i).getBusLines().size();
        }
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stationName;
        public RelativeLayout busLineRoot;
        public TextView busLineName;
        public TextView busLineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stationName = itemView.findViewById(R.id.station_name);
            busLineRoot = itemView.findViewById(R.id.bus_line_root);
            busLineName = itemView.findViewById(R.id.bus_line_name);
            busLineStatus = itemView.findViewById(R.id.bus_line_status);
        }
    }
}
