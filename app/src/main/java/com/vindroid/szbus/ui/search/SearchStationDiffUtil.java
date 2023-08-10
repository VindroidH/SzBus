package com.vindroid.szbus.ui.search;

import com.vindroid.szbus.model.StationDetail;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class SearchStationDiffUtil extends DiffUtil.Callback {

    private final List<StationDetail> mOldList;
    private final List<StationDetail> mNewList;

    public SearchStationDiffUtil(List<StationDetail> oldList, List<StationDetail> newList) {
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
            StationDetail oldStation = mOldList.get(oldItemPosition);
            StationDetail newStation = mNewList.get(newItemPosition);
            return newStation.getName().equals(oldStation.getName())
                    && newStation.getAddress().equals(oldStation.getAddress());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
