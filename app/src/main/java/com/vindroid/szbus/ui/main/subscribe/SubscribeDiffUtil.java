package com.vindroid.szbus.ui.main.subscribe;

import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class SubscribeDiffUtil extends DiffUtil.Callback {
    private final List<Subscribe> mOldList;
    private final List<Subscribe> mNewList;

    public SubscribeDiffUtil(List<Subscribe> oldList, List<Subscribe> newList) {
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
        SubscribeBusLine oldBusLine = null;
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
        if (oldBusLine == null) oldBusLine = new SubscribeBusLine();

        Station newStation = null;
        SubscribeBusLine newBusLine = null;
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
        if (newBusLine == null) newBusLine = new SubscribeBusLine();

        try {
            return newStation.getId().equals(oldStation.getId())
                    && newBusLine.getId().equals(oldBusLine.getId());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Station oldStation = null;
        SubscribeBusLine oldBusLine = null;
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
        if (oldBusLine == null) oldBusLine = new SubscribeBusLine();

        Station newStation = null;
        SubscribeBusLine newBusLine = null;
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
        if (newBusLine == null) newBusLine = new SubscribeBusLine();

        try {
            return newStation.getName().equals(oldStation.getName())
                    && newBusLine.getName().equals(oldBusLine.getName())
                    && newBusLine.getAhead() == oldBusLine.getAhead();
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
