package com.vindroid.szbus.ui.main.favorite;

import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class FavoriteListDiffUtil extends DiffUtil.Callback {
    private final List<Favorite> mOldList;
    private final List<Favorite> mNewList;

    public FavoriteListDiffUtil(List<Favorite> oldList, List<Favorite> newList) {
        mOldList = oldList == null ? new ArrayList<>() : oldList;
        mNewList = newList == null ? new ArrayList<>() : newList;
    }

    @Override
    public int getOldListSize() {
        int count = 0;
        for (int i = 0; i < mOldList.size(); i++) {
            count += mOldList.get(i).getBusLines().size();
        }
        return count;
    }

    @Override
    public int getNewListSize() {
        int count = 0;
        for (int i = 0; i < mNewList.size(); i++) {
            count += mNewList.get(i).getBusLines().size();
        }
        return count;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Station oldStation = null;
        InComingBusLine oldBusLine = null;
        int oldIndex = -1;
        for (int i = 0, p = 0; i < mOldList.size(); i++) {
            for (int j = 0; j < mOldList.get(i).getBusLines().size(); j++, p++) {
                if (p == oldItemPosition) {
                    oldBusLine = mOldList.get(i).getBusLine(j);
                    oldStation = mOldList.get(i).getStation();
                    oldIndex = mOldList.get(i).getIndex();
                    break;
                }
            }
            if (oldBusLine != null) {
                break;
            }
        }
        if (oldStation == null || oldBusLine == null) return false;

        Station newStation = null;
        InComingBusLine newBusLine = null;
        int newIndex = -1;
        for (int i = 0, p = 0; i < mNewList.size(); i++) {
            for (int j = 0; j < mNewList.get(i).getBusLines().size(); j++, p++) {
                if (p == newItemPosition) {
                    newBusLine = mNewList.get(i).getBusLine(j);
                    newStation = mNewList.get(i).getStation();
                    newIndex = mNewList.get(i).getIndex();
                    break;
                }
            }
            if (newBusLine != null) {
                break;
            }
        }
        if (newStation == null || newBusLine == null) return false;

        return newStation.getId().equals(oldStation.getId())
                && newBusLine.getId().equals(oldBusLine.getId())
                && newIndex == oldIndex;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Station oldStation = null;
        InComingBusLine oldBusLine = null;
        for (int i = 0, position = 0; i < mOldList.size(); i++) {
            for (int j = 0; j < mOldList.get(i).getBusLines().size(); j++) {
                if (position == oldItemPosition) {
                    oldBusLine = mOldList.get(i).getBusLine(j);
                    oldStation = mOldList.get(i).getStation();
                    break;
                }
                position += 1;
            }
            if (oldBusLine != null) {
                break;
            }
        }
        if (oldStation == null || oldBusLine == null) return false;

        Station newStation = null;
        InComingBusLine newBusLine = null;
        for (int i = 0, position = 0; i < mNewList.size(); i++) {
            for (int j = 0; j < mNewList.get(i).getBusLines().size(); j++) {
                if (position == newItemPosition) {
                    newBusLine = mNewList.get(i).getBusLine(j);
                    newStation = mNewList.get(i).getStation();
                    break;
                }
                position += 1;
            }
            if (newBusLine != null) {
                break;
            }
        }
        if (newStation == null || newBusLine == null) return false;

        return newStation.getName().equals(oldStation.getName())
                && newBusLine.getName().equals(oldBusLine.getName())
                && newBusLine.getComing() == oldBusLine.getComing();
    }
}
