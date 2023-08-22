package com.vindroid.szbus.ui.choose;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vindroid.szbus.databinding.ListItemBusLineBinding;
import com.vindroid.szbus.databinding.StubItemAheadBinding;
import com.vindroid.szbus.databinding.StubItemCheckboxBinding;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.ui.busline.BusLineActivity;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseBusLineAdapter extends RecyclerView.Adapter<ChooseBusLineAdapter.ViewHolder> {
    private final String mType;
    private List<InComingBusLine> mBusLines;
    private final Map<String, InComingBusLine> mSelectedItems;
    private final Map<String, String> mExtraValues;

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

        holder.binding.busLineRoot.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BusLineActivity.class);
            intent.putExtra(Constants.KEY_ID, info.getId());
            intent.putExtra(Constants.KEY_NAME, info.getName());
            holder.itemView.getContext().startActivity(intent);
        });

        if (holder.checkboxBinding != null) {
            holder.checkboxBinding.checkbox.setChecked(mSelectedItems.containsKey(info.getId()));
            holder.checkboxBinding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mSelectedItems.put(info.getId(), info);
                    if (holder.aheadBinding != null) {
                        String text = holder.aheadBinding.input.getText().toString();
                        mExtraValues.put(info.getId(),
                                TextUtils.isEmpty(text) ? String.valueOf(Constants.DEFAULT_AHEAD) : text);
                    }
                } else {
                    mSelectedItems.remove(info.getId());
                    if (holder.aheadBinding != null) {
                        mExtraValues.remove(info.getId());
                    }
                }
            });
        }

        if (holder.aheadBinding != null) {
            if (mExtraValues.containsKey(info.getId())) {
                holder.aheadBinding.input.setText(mExtraValues.get(info.getId()));
            }
            holder.aheadBinding.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (TextUtils.isEmpty(s)) {
                        mExtraValues.put(info.getId(), String.valueOf(Constants.DEFAULT_AHEAD));
                    } else {
                        mExtraValues.put(info.getId(), s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.checkboxBinding != null) {
            holder.checkboxBinding.checkbox.setOnCheckedChangeListener(null);
        }
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

            this.binding.stubCheckbox.setOnInflateListener(
                    (stub, inflated) -> checkboxBinding = StubItemCheckboxBinding.bind(inflated));
            this.binding.stubCheckbox.inflate();

            if (Constants.TYPE_SUBSCRIBE.equals(type)) {
                this.binding.stubAhead.setOnInflateListener(
                        (stub, inflated) -> aheadBinding = StubItemAheadBinding.bind(inflated));
                this.binding.stubAhead.inflate();
            }
        }
    }
}
