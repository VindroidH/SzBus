package com.vindroid.szbus.ui.choose;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bus_line, parent, false);
        return new ViewHolder(view, mType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InComingBusLine info = mBusLines.get(position);
        holder.busLineName.setText(info.getName());
        holder.busLineTo.setText(info.getEndStationName());
        holder.checkBox.setChecked(mSelectedItems.containsKey(info.getId()));
        if (Constants.TYPE_SUBSCRIBE.equals(mType)) {
            if (mExtraValues.containsKey(info.getId())) {
                holder.aheadInput.setText(mExtraValues.get(info.getId()));
            }
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mSelectedItems.put(info.getId(), info);
                if (Constants.TYPE_SUBSCRIBE.equals(mType)) {
                    String text = holder.aheadInput.getText().toString();
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
        holder.itemView.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return mBusLines.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView busLineName;
        public TextView busLineTo;
        public EditText aheadInput;
        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView, String type) {
            super(itemView);
            busLineName = itemView.findViewById(R.id.bus_line_name);
            busLineTo = itemView.findViewById(R.id.bus_line_to);

            ViewStub viewStub;
            if (Constants.TYPE_SUBSCRIBE.equals(type)) {
                viewStub = itemView.findViewById(R.id.stub_ahead);
                viewStub.inflate();
                aheadInput = itemView.findViewById(R.id.input);
            } else {
                viewStub = itemView.findViewById(R.id.stub_checkbox);
                viewStub.inflate();
            }
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
