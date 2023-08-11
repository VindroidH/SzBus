package com.vindroid.szbus.ui.main.subscribe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.AdapterListener;
import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ListItemSubscribeBinding;
import com.vindroid.szbus.databinding.StubItemSubscribeHeaderBinding;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder> {
    private static final String TAG;

    private final AdapterListener mListener;
    private List<Subscribe> mSubscribes;

    static {
        TAG = App.getTag(SubscribeAdapter.class.getSimpleName());
    }

    public SubscribeAdapter(AdapterListener listener) {
        mListener = listener;
        mSubscribes = new ArrayList<>();
    }

    public void updateData(List<Subscribe> subscribes) {
        try {
            mSubscribes = new ArrayList<>();
            for (Subscribe subscribe : subscribes) {
                mSubscribes.add((Subscribe) subscribe.clone());
            }
        } catch (CloneNotSupportedException e) {
            Log.e(TAG, "[updateData] cannot deep copy data");
            mSubscribes = new ArrayList<>(subscribes);
        }
    }

    public List<Subscribe> getData() {
        return mSubscribes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSubscribeBinding binding = ListItemSubscribeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = null;
        SubscribeBusLine busLine = null;
        boolean isHeader = false;

        int index = 0;
        for (int i = 0; i < mSubscribes.size(); i++) {
            for (int j = 0; j < mSubscribes.get(i).getBusLines().size(); j++) {
                if (index == position) {
                    isHeader = j == 0;
                    station = mSubscribes.get(i).getStation();
                    busLine = mSubscribes.get(i).getBusLine(j);
                    break;
                }
                index += 1;
            }
            if (busLine != null) {
                break;
            }
        }
        if (station == null) station = new Station();
        if (busLine == null) busLine = new SubscribeBusLine();

        if (isHeader) {
            holder.binding.stubHeader.inflate();
            if (holder.headerBinding != null) {
                holder.headerBinding.stationName.setText(station.getName());
            }
        }
        holder.binding.busLineName.setText(busLine.getName());
        holder.binding.busLineAhead.setText(
                App.getStringById(R.string.ahead_info, busLine.getAhead()));

        final Station finalStation = station;
        holder.itemView.setOnLongClickListener(v -> {
            if (mListener != null) mListener.onItemLongClicked(position, finalStation.getId());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (int i = 0; i < mSubscribes.size(); i++) {
            count += mSubscribes.get(i).getBusLines().size();
        }
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemSubscribeBinding binding;
        StubItemSubscribeHeaderBinding headerBinding;

        public ViewHolder(ListItemSubscribeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.stubHeader.setOnInflateListener((stub, inflated) ->
                    headerBinding = StubItemSubscribeHeaderBinding.bind(inflated));
        }
    }
}
