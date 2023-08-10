package com.vindroid.szbus.ui.main.subscribe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.databinding.ListItemSubscribeBinding;
import com.vindroid.szbus.model.Subscribe;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribeAdapter extends RecyclerView.Adapter<SubscribeAdapter.ViewHolder> {
    private final static String TAG;

    private LinkedList<Subscribe> mSubscribeList;

    static {
        TAG = App.getTag(SubscribeAdapter.class.getSimpleName());
    }

    public SubscribeAdapter() {
        mSubscribeList = new LinkedList<>();
    }

    public void updateData(LinkedList<Subscribe> subscribeList) {
        mSubscribeList = new LinkedList<>(subscribeList);
    }

    public LinkedList<Subscribe> getData() {
        return mSubscribeList;
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
        // TODO
//        Subscribe subscribe = mSubscribeList.get(position);
//        if (position == 0
//                || mSubscribeList.get(position - 1).getStation().getId().equals(subscribe.getStation().getId())) {
//            ViewStub view = holder.itemView.findViewById(R.id.header);
//            view.inflate();
//            holder.stationName = view.findViewById(R.id.station_name);
//            holder.notifyStartTime = view.findViewById(R.id.notify_time_start);
//            holder.notifyEndTime = view.findViewById(R.id.notify_time_end);
//
//            holder.stationName.setText(subscribe.getStation().getName());
//            holder.notifyStartTime.setText(subscribe.getStartTime());
//            holder.notifyEndTime.setText(subscribe.getEndTime());
//            // TODO
////            holder.headerNotifyDate.setText("TODO");
//
//        }
//
//        holder.busLineName.setText(subscribe.getBusLine().getName());
//        holder.busLineAhead.setText(String.valueOf(subscribe.getAhead()));
    }

    @Override
    public int getItemCount() {
        return mSubscribeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemSubscribeBinding binding;

        public ViewHolder(ListItemSubscribeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
