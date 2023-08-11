package com.vindroid.szbus.ui.main.favorite;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vindroid.szbus.AdapterListener;
import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ListItemFavoriteBinding;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder> {
    private final static String TAG;

    private final AdapterListener mListener;
    private List<Favorite> mFavoriteList;

    static {
        TAG = App.getTag(FavoriteListAdapter.class.getSimpleName());
    }

    public FavoriteListAdapter(AdapterListener listener) {
        mListener = listener;
        mFavoriteList = new ArrayList<>();
    }

    public void updateData(List<Favorite> favoriteList) {
        try {
            mFavoriteList = new ArrayList<>();
            for (Favorite favorite : favoriteList) {
                mFavoriteList.add((Favorite) favorite.clone());
            }
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "[updateData] cannot deep copy data");
            mFavoriteList = new LinkedList<>(favoriteList);
        }
    }

    public List<Favorite> getData() {
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
        boolean isHeader = false;

        int index = 0;
        for (int i = 0; i < mFavoriteList.size(); i++) {
            for (int j = 0; j < mFavoriteList.get(i).getBusLines().size(); j++) {
                if (index == position) {
                    isHeader = j == 0;
                    station = mFavoriteList.get(i).getStation();
                    busLine = mFavoriteList.get(i).getBusLine(j);
                    break;
                }
                index += 1;
            }
            if (busLine != null) {
                break;
            }
        }
        if (station == null) station = new Station();
        if (busLine == null) busLine = new InComingBusLine();

        if (isHeader) {
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

        final Station finalStation = station;
        final InComingBusLine finalBusLine = busLine;
        if (isHeader) {
            holder.binding.stationName.setOnClickListener(v -> {
                if (mListener != null) mListener.onItemClicked(position, finalStation);
            });
            holder.binding.stationName.setOnLongClickListener(v -> {
                if (mListener != null) mListener.onItemLongClicked(position, finalStation.getId());
                return false;
            });
        }
        holder.binding.busLineRoot.setOnClickListener(v -> {
            if (mListener != null) mListener.onItemClicked(position, finalBusLine);
        });
        holder.binding.busLineRoot.setOnLongClickListener(v -> {
            if (mListener != null) mListener.onItemLongClicked(position, finalStation.getId());
            return false;
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
