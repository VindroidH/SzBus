package com.vindroid.szbus.ui.station;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.ui.busline.BusLineActivity;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ListItemBusLineBinding;
import com.vindroid.szbus.databinding.StubItemTextviewBinding;
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
        ListItemBusLineBinding binding = ListItemBusLineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InComingBusLine busLine = mBusLines.get(position);
        holder.binding.busLineName.setText(busLine.getName());
        holder.binding.busLineTo.setText(busLine.getEndStationName());
        int coming = busLine.getComing();
        if (coming == InComingBusLine.COMING_ERR) {
            holder.textViewBinding.status.setText(R.string.coming_err);
            holder.textViewBinding.status.setTextColor(App.getColorById(R.color.red));
        } else if (coming == InComingBusLine.COMING_NO) {
            holder.textViewBinding.status.setText(R.string.coming_not);
        } else if (coming == InComingBusLine.COMING_NOW) {
            holder.textViewBinding.status.setText(R.string.coming_now);
            holder.textViewBinding.status.setTextColor(App.getColorById(R.color.red));
        } else {
            holder.textViewBinding.status.setText(String.format(App.getStringById(R.string.coming_still), coming));
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
        ListItemBusLineBinding binding;
        StubItemTextviewBinding textViewBinding;

        public ViewHolder(ListItemBusLineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.stubInfo.setOnInflateListener(
                    (stub, inflated) -> textViewBinding = StubItemTextviewBinding.bind(inflated));
            binding.stubInfo.inflate();
        }
    }
}
