package com.vindroid.szbus.ui.choose;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.databinding.ListItemBusLineBinding;
import com.vindroid.szbus.databinding.StubItemAheadBinding;
import com.vindroid.szbus.databinding.StubItemCheckboxBinding;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseBusLineAdapter extends RecyclerView.Adapter<ChooseBusLineAdapter.ViewHolder> {
    private final static String TAG;

    private final String mType;
    private List<InComingBusLine> mBusLines;
    private final Map<String, InComingBusLine> mSelectedItems;
    private final Map<String, String> mExtraValues;

    static {
        TAG = App.getTag(ChooseBusLineAdapter.class.getSimpleName());
    }

    public ChooseBusLineAdapter(String type) {
        mType = type;
        mBusLines = new LinkedList<>();
        mSelectedItems = new HashMap<>();
        mExtraValues = new HashMap<>();
    }

    public void updateData(List<InComingBusLine> busLines) {
        mBusLines = new ArrayList<>(busLines);
    }

    public List<InComingBusLine> getData() {
        return mBusLines;
    }

    public List<InComingBusLine> getSelectedItems() {
        return new ArrayList<>(mSelectedItems.values());
    }

    public Map<String, String> getExtraValues() {
        return mExtraValues;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBusLineBinding binding = ListItemBusLineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, mType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InComingBusLine info = mBusLines.get(position);
        holder.binding.busLineName.setText(info.getName());
        holder.binding.busLineTo.setText(info.getEndStationName());

        holder.checkboxBinding.checkbox.setChecked(mSelectedItems.containsKey(info.getId()));
        if (Constants.TYPE_SUBSCRIBE.equals(mType)) {
            if (mExtraValues.containsKey(info.getId())) {
                holder.aheadBinding.input.setText(mExtraValues.get(info.getId()));
            }
        }

        holder.checkboxBinding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSelectedItems.put(info.getId(), info);
                if (Constants.TYPE_SUBSCRIBE.equals(mType)) {
                    String text = holder.aheadBinding.input.getText().toString();
                    if (TextUtils.isEmpty(text)) text = String.valueOf(Constants.DEFAULT_AHEAD);
                    mExtraValues.put(info.getId(), text);
                }
            } else {
                mSelectedItems.remove(info.getId());
                if (Constants.TYPE_SUBSCRIBE.equals(mType)) {
                    mExtraValues.remove(info.getId());
                }
            }
        });

        holder.itemView.setOnClickListener(v -> holder.checkboxBinding.checkbox.setChecked(
                !holder.checkboxBinding.checkbox.isChecked()));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.checkboxBinding.checkbox.setOnCheckedChangeListener(null);
    }

    @Override
    public int getItemCount() {
        return mBusLines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemBusLineBinding binding;
        StubItemCheckboxBinding checkboxBinding;
        StubItemAheadBinding aheadBinding;

        public ViewHolder(ListItemBusLineBinding binding, String type) {
            super(binding.getRoot());
            this.binding = binding;

            binding.stubCheckbox.setOnInflateListener(
                    (stub, inflated) -> checkboxBinding = StubItemCheckboxBinding.bind(inflated));
            binding.stubCheckbox.inflate();

            if (Constants.TYPE_SUBSCRIBE.equals(type)) {
                binding.stubAhead.setOnInflateListener(
                        (stub, inflated) -> aheadBinding = StubItemAheadBinding.bind(inflated));
                binding.stubAhead.inflate();
            }
        }
    }
}
