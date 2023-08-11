package com.vindroid.szbus.ui.search;

import com.vindroid.szbus.model.BusLine;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class SearchBusLineDiffUtil extends DiffUtil.Callback {
    private final List<BusLine> mOldList;
    private final List<BusLine> mNewList;

    public SearchBusLineDiffUtil(List<BusLine> oldList, List<BusLine> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            return mNewList.get(newItemPosition).getId().equals(mOldList.get(oldItemPosition).getId());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            BusLine oldLine = mOldList.get(oldItemPosition);
            BusLine newLine = mNewList.get(newItemPosition);
            return newLine.getName().equals(oldLine.getName())
                    && newLine.getStartStationName().equals(oldLine.getStartStationName())
                    && newLine.getEndStationName().equals(oldLine.getEndStationName());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
