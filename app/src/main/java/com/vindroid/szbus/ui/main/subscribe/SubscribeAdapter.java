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
import com.vindroid.szbus.utils.Constants;

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
        Subscribe subscribe = null;
        SubscribeBusLine busLine = null;
        boolean isHeader = false;

        int index = 0;
        for (int i = 0; i < mSubscribes.size(); i++) {
            for (int j = 0; j < mSubscribes.get(i).getBusLines().size(); j++) {
                if (index == position) {
                    isHeader = j == 0;
                    subscribe = mSubscribes.get(i);
                    busLine = subscribe.getBusLine(j);
                    break;
                }
                index += 1;
            }
            if (busLine != null) {
                break;
            }
        }
        if (subscribe == null) subscribe = new Subscribe();
        if (busLine == null) busLine = new SubscribeBusLine();

        if (isHeader) {
            holder.binding.stubHeader.inflate();
            if (holder.headerBinding != null) {
                holder.headerBinding.stationName.setText(subscribe.getStation().getName());
                holder.headerBinding.notifyTimeStart.setText(subscribe.getStartTime());
                holder.headerBinding.notifyTimeEnd.setText(subscribe.getEndTime());
                String date = "";
                int bit = Integer.parseInt(String.valueOf(subscribe.getWeekBit()), 2);
                if ((bit & Constants.SUNDAY_BIT) == Constants.SUNDAY_BIT) {
                    date += App.getStringById(R.string.sunday);
                }
                if ((bit & Constants.MONDAY_BIT) == Constants.MONDAY_BIT) {
                    date += App.getStringById(R.string.monday) + " ";
                }
                if ((bit & Constants.TUESDAY_BIT) == Constants.TUESDAY_BIT) {
                    date += App.getStringById(R.string.tuesday) + " ";
                }
                if ((bit & Constants.WEDNESDAY_BIT) == Constants.WEDNESDAY_BIT) {
                    date += App.getStringById(R.string.wednesday) + " ";
                }
                if ((bit & Constants.THURSDAY_BIT) == Constants.THURSDAY_BIT) {
                    date += App.getStringById(R.string.thursday) + " ";
                }
                if ((bit & Constants.FRIDAY_BIT) == Constants.FRIDAY_BIT) {
                    date += App.getStringById(R.string.friday) + " ";
                }
                if ((bit & Constants.SATURDAY_BIT) == Constants.SATURDAY_BIT) {
                    date += App.getStringById(R.string.saturday) + " ";
                }
                holder.headerBinding.notifyDate.setText(date.trim());
            }
        }
        holder.binding.busLineName.setText(busLine.getName());
        holder.binding.busLineAhead.setText(
                App.getStringById(R.string.ahead_info, busLine.getAhead()));

        final Station finalStation = subscribe.getStation();
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
