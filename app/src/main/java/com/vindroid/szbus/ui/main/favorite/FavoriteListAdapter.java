package com.vindroid.szbus.ui.main.favorite;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ListItemFavoriteBinding;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.ui.busline.BusLineActivity;
import com.vindroid.szbus.ui.station.StationActivity;
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
            for (Favorite favorite : favoriteList) {
                mFavoriteList.add((Favorite) favorite.clone());
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
        ListItemFavoriteBinding binding = ListItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
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
            holder.binding.stationName.setVisibility(View.VISIBLE);
            holder.binding.stationName.setText(station.getName());
        } else {
            holder.binding.stationName.setVisibility(View.GONE);
        }

        holder.binding.busLineName.setText(busLine.getName());
        int coming = busLine.getComing();
        if (coming == InComingBusLine.COMING_ERR) {
            holder.binding.busLineStatus.setText(R.string.coming_err);
            holder.binding.busLineStatus.setTextColor(App.getColorById(R.color.red));
        } else if (coming == InComingBusLine.COMING_NO) {
            holder.binding.busLineStatus.setText(R.string.coming_not);
        } else if (coming == InComingBusLine.COMING_NOW) {
            holder.binding.busLineStatus.setText(R.string.coming_now);
            holder.binding.busLineStatus.setTextColor(App.getColorById(R.color.red));
        } else {
            holder.binding.busLineStatus.setText(String.format(App.getStringById(R.string.coming_still), coming));
        }

        if (station != null) {
            final Station fStation = station;
            holder.binding.stationName.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.KEY_ID, fStation.getId());
                intent.putExtra(Constants.KEY_NAME, fStation.getName());
                intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_FAVORITE);
                v.getContext().startActivity(intent);
            });
        }
        final InComingBusLine fBusLine = busLine;
        holder.binding.busLineRoot.setOnClickListener(v -> {
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
        ListItemFavoriteBinding binding;

        public ViewHolder(ListItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
