package com.vindroid.szbus.ui.main.favorite;

import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.Station;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class FavoriteListDiffUtil extends DiffUtil.Callback {
    private final List<Favorite> mOldList;
    private final List<Favorite> mNewList;

    public FavoriteListDiffUtil(List<Favorite> oldList, List<Favorite> newList) {
        mOldList = oldList;
        mNewList = newList;
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
        int index = 0;
        for (int i = 0; i < mOldList.size(); i++) {
            for (int j = 0; j < mOldList.get(i).getBusLines().size(); j++) {
                if (index == oldItemPosition) {
                    oldBusLine = mOldList.get(i).getBusLine(j);
                    if (j == 0) {
                        oldStation = mOldList.get(i).getStation();
                        oldIndex = mOldList.get(i).getIndex();
                    }
                    break;
                }
                index += 1;
            }
            if (oldBusLine != null) {
                break;
            }
        }
        if (oldStation == null) oldStation = new Station();
        if (oldBusLine == null) oldBusLine = new InComingBusLine();

        Station newStation = null;
        InComingBusLine newBusLine = null;
        int newIndex = -1;
        index = 0;
        for (int i = 0; i < mNewList.size(); i++) {
            for (int j = 0; j < mNewList.get(i).getBusLines().size(); j++) {
                if (index == newItemPosition) {
                    newBusLine = mNewList.get(i).getBusLine(j);
                    if (j == 0) {
                        newStation = mNewList.get(i).getStation();
                        newIndex = mNewList.get(i).getIndex();
                    }
                    break;
                }
                index += 1;
            }
            if (newBusLine != null) {
                break;
            }
        }
        if (newStation == null) newStation = new Station();
        if (newBusLine == null) newBusLine = new InComingBusLine();

        try {
            return newStation.getId().equals(oldStation.getId())
                    && newBusLine.getId().equals(oldBusLine.getId())
                    && newIndex == oldIndex;
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Station oldStation = null;
        InComingBusLine oldBusLine = null;
        int index = 0;
        for (int i = 0; i < mOldList.size(); i++) {
            for (int j = 0; j < mOldList.get(i).getBusLines().size(); j++) {
                if (index == oldItemPosition) {
                    oldBusLine = mOldList.get(i).getBusLine(j);
                    if (j == 0) {
                        oldStation = mOldList.get(i).getStation();
                    }
                    break;
                }
                index += 1;
            }
            if (oldBusLine != null) {
                break;
            }
        }
        if (oldStation == null) oldStation = new Station();
        if (oldBusLine == null) oldBusLine = new InComingBusLine();

        Station newStation = null;
        InComingBusLine newBusLine = null;
        index = 0;
        for (int i = 0; i < mNewList.size(); i++) {
            for (int j = 0; j < mNewList.get(i).getBusLines().size(); j++) {
                if (index == newItemPosition) {
                    newBusLine = mNewList.get(i).getBusLine(j);
                    if (j == 0) {
                        newStation = mNewList.get(i).getStation();
                    }
                    break;
                }
                index += 1;
            }
            if (newBusLine != null) {
                break;
            }
        }
        if (newStation == null) newStation = new Station();
        if (newBusLine == null) newBusLine = new InComingBusLine();

        try {
            return newStation.getName().equals(oldStation.getName())
                    && newBusLine.getName().equals(oldBusLine.getName())
                    && newBusLine.getComing() == oldBusLine.getComing();
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
