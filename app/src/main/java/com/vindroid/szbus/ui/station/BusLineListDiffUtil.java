package com.vindroid.szbus.ui.station;

import com.vindroid.szbus.model.InComingBusLine;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class BusLineListDiffUtil extends DiffUtil.Callback {

    private final List<InComingBusLine> mOldList;
    private final List<InComingBusLine> mNewList;

    public BusLineListDiffUtil(List<InComingBusLine> oldList, List<InComingBusLine> newList) {
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
            return mNewList.get(newItemPosition).getId()
                    .equals(mOldList.get(oldItemPosition).getId());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            InComingBusLine oldBusLine = mOldList.get(oldItemPosition);
            InComingBusLine newBusLine = mNewList.get(newItemPosition);
            return newBusLine.getName().equals(oldBusLine.getName())
                    && newBusLine.getEndStationName().equals(oldBusLine.getEndStationName())
                    && newBusLine.getComing() == oldBusLine.getComing();
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
