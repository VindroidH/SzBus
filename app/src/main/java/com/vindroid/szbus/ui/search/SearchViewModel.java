package com.vindroid.szbus.ui.search;

import com.vindroid.szbus.App;
import com.vindroid.szbus.model.BusLine;
import com.vindroid.szbus.model.StationDetail;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SearchViewModel extends ViewModel {
    private final static String TAG;

    public static final int TYPE_BUS_LINE = 0;
    public static final int TYPE_STATION = 1;

    private final MutableLiveData<List<BusLine>> mBusLineList = new MutableLiveData<>();
    private final MutableLiveData<List<StationDetail>> mStationList = new MutableLiveData<>();

    static {
        TAG = App.getTag(SearchViewModel.class.getSimpleName());
    }

    public void updateBusLineList(List<BusLine> list) {
        mBusLineList.setValue(list);
    }

    public MutableLiveData<List<BusLine>> getBusLineList() {
        return mBusLineList;
    }

    public void updateStationList(List<StationDetail> list) {
        mStationList.setValue(list);
    }

    public MutableLiveData<List<StationDetail>> getStationList() {
        return mStationList;
    }
}
