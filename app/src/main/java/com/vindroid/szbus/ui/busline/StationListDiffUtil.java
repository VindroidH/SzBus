package com.vindroid.szbus.ui.busline;

import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.Station;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.DiffUtil;

public class StationListDiffUtil extends DiffUtil.Callback {
    private final LinkedList<Station> mOldStations;
    private final LinkedList<Station> mNewStations;
    private final Map<String, RunningBus> mOldStationBusMapping;
    private final Map<String, RunningBus> mNewStationBusMapping;

    public StationListDiffUtil(LinkedList<Station> oldStations, LinkedList<Station> newStations,
                               List<RunningBus> oldRunningBuses, List<RunningBus> newRunningBuses) {
        mOldStations = oldStations;
        mNewStations = newStations;
        mOldStationBusMapping = new HashMap<>();
        mNewStationBusMapping = new HashMap<>();

        for (RunningBus bus : oldRunningBuses) {
            for (Station station : oldStations) {
                if (bus.getStationId().equals(station.getId())) {
                    mOldStationBusMapping.put(bus.getStationId(), bus);
                    break;
                }
            }
        }

        for (RunningBus bus : newRunningBuses) {
            for (Station station : newStations) {
                if (bus.getStationId().equals(station.getId())) {
                    mNewStationBusMapping.put(bus.getStationId(), bus);
                    break;
                }
            }
        }
    }

    @Override
    public int getOldListSize() {
        return mOldStations.size();
    }

    @Override
    public int getNewListSize() {
        return mNewStations.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            return mNewStations.get(newItemPosition).getId()
                    .equals(mOldStations.get(oldItemPosition).getId());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            Station oldStation = mOldStations.get(oldItemPosition);
            RunningBus oldBus = mOldStationBusMapping.get(oldStation.getId());
            if (oldBus == null) oldBus = new RunningBus();
            Station newStation = mNewStations.get(newItemPosition);
            RunningBus newBus = mNewStationBusMapping.get(newStation.getId());
            if (newBus == null) newBus = new RunningBus();
            return newStation.getName().equals(oldStation.getName())
                    && newBus.getInfo().equals(oldBus.getInfo())
                    && newBus.getInTime().equals(oldBus.getInTime());
        } catch (IndexOutOfBoundsException | NullPointerException ignore) {
        }
        return false;
    }
}
