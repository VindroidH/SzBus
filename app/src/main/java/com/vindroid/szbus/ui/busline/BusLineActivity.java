package com.vindroid.szbus.ui.busline;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ActivityBusLineBinding;
import com.vindroid.szbus.model.BusLineDetail;
import com.vindroid.szbus.model.BusLineRealTimeInfo;
import com.vindroid.szbus.model.RunningBus;
import com.vindroid.szbus.model.Station;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DiffUtil;

public class BusLineActivity extends AppCompatActivity
        implements View.OnClickListener, Toolbar.OnMenuItemClickListener,
        BusCenter.GetBusLineListener, BusCenter.GetBusLineRealTimeInfoListener {
    private final static String TAG;

    private ActivityBusLineBinding mBinding;
    private StationListAdapter mAdapter;

    private BusLineDetail mBusLineDetail = null;
    private BusLineRealTimeInfo mRealTimeInfo = null;
    private boolean mIsRefreshing = false;
    private boolean mIsGetBusLineDone = false;
    private long mUpdateTimeMills;

    static {
        TAG = App.getTag(BusLineActivity.class.getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityBusLineBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.loading.show();

        mBusLineDetail = new BusLineDetail();
        mBusLineDetail.setId(getIntent().getStringExtra(Constants.KEY_ID));
        mBusLineDetail.setName(getIntent().getStringExtra(Constants.KEY_NAME));
        Log.d(TAG, "[onCreate] id: " + mBusLineDetail.getId() + ", name: " + mBusLineDetail.getName());

        mBinding.toolbar.setTitle(mBusLineDetail.getName());
        mBinding.toolbar.setOnMenuItemClickListener(this);
        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mBinding.refresh.setOnClickListener(this);

        mAdapter = new StationListAdapter();
        mBinding.stationList.setAdapter(mAdapter);

        refreshBusLineInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mUpdateTimeMills >= Constants.REFRESH_MIN_INTERVAL) {
            refreshBusInfo();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mBusLineDetail = new BusLineDetail();
        mBusLineDetail.setId(getIntent().getStringExtra(Constants.KEY_ID));
        mBusLineDetail.setName(getIntent().getStringExtra(Constants.KEY_NAME));
        Log.d(TAG, "[onNewIntent] id: " + mBusLineDetail.getId() + ", name: " + mBusLineDetail.getName());

        refreshBusLineInfo();
        refreshBusInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.refresh.getId()) {
            if (!mIsRefreshing) {
                if (!mIsGetBusLineDone) {
                    refreshBusLineInfo();
                }
                refreshBusInfo();
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.menu_reverse) {
            if (TextUtils.isEmpty(mBusLineDetail.getReverseId())) {
                Toast.makeText(this, R.string.no_reverse_bus_line, Toast.LENGTH_LONG).show();
                return false;
            }
            mBusLineDetail.setId(mBusLineDetail.getReverseId());
            refreshBusLineInfo();
            refreshBusInfo();
        }
        return false;
    }

    @Override
    public void onGetBusLineCompleted(boolean result, BusLineDetail busLineDetail, String msg) {
        Log.d(TAG, "[onGetBusLineCompleted] result: " + result);
        mIsGetBusLineDone = result;
        if (result) {
            mBusLineDetail = busLineDetail;
            updateBusLineStatus();
            updateList();
        }
        mBinding.contentRoot.setVisibility(View.VISIBLE);
        mBinding.loading.hide();
    }

    @Override
    public void onGetBusLineRealTimeInfoCompleted(boolean result, BusLineRealTimeInfo info, String msg) {
        mIsRefreshing = false;
        mUpdateTimeMills = System.currentTimeMillis();
        stopLoading();
        if (result) {
            mRealTimeInfo = info;
            mBinding.busLineRoot.nextDepartTime.setText(mRealTimeInfo.getNextDepartTime());
            mBinding.busLineRoot.updateTime.setText(Utils.getTime(Constants.UPDATE_TIME_FORMAT));
            updateList();
        }
    }

    private void refreshBusLineInfo() {
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetBusLine(this);
        task.execute(mBusLineDetail.getId());
    }

    private void refreshBusInfo() {
        mIsRefreshing = true;
        startLoading();
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetBusLineRealTimeInfo(this);
        task.execute(mBusLineDetail.getId());
    }

    private void updateBusLineStatus() {
        if (!TextUtils.isEmpty(mBusLineDetail.getStartStationName())) {
            mBinding.busLineRoot.stationStartName.setVisibility(View.VISIBLE);
            mBinding.busLineRoot.stationStartName.setText(mBusLineDetail.getStartStationName());
        } else {
            if (mBusLineDetail.getStations().size() > 0) {
                mBinding.busLineRoot.stationStartName.setVisibility(View.VISIBLE);
                mBinding.busLineRoot.stationStartName.setText(
                        mBusLineDetail.getStations().get(0).getName());
            } else {
                mBinding.busLineRoot.stationStartName.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(mBusLineDetail.getEndStationName())) {
            mBinding.busLineRoot.stationEndRoot.setVisibility(View.VISIBLE);
            mBinding.busLineRoot.stationEndName.setText(mBusLineDetail.getEndStationName());
        } else {
            if (mBusLineDetail.getStations().size() > 0) {
                mBinding.busLineRoot.stationEndRoot.setVisibility(View.VISIBLE);
                mBinding.busLineRoot.stationEndName.setText(
                        mBusLineDetail.getStations().get(mBusLineDetail.getStations().size() - 1).getName());
            } else {
                mBinding.busLineRoot.stationEndRoot.setVisibility(View.GONE);
            }
        }

        mBinding.busLineRoot.busFirstTime.setText(mBusLineDetail.getFirstTime());
        mBinding.busLineRoot.busLastTime.setText(mBusLineDetail.getLastTime());
        mBinding.busLineRoot.updateTime.setText(Utils.getTime(Constants.UPDATE_TIME_FORMAT));
    }

    private void updateList() {
        LinkedList<Station> newStations = mBusLineDetail == null ? new LinkedList<>() : mBusLineDetail.getStations();
        List<RunningBus> newBusList = mRealTimeInfo == null ? new ArrayList<>() : mRealTimeInfo.getRunningBuses();
        StationListDiffUtil diffUtil = new StationListDiffUtil(
                mAdapter.getStations(), newStations,
                mAdapter.getRunningBuses(), newBusList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        if (mBusLineDetail != null) {
            mAdapter.updateStation(mBusLineDetail.getStations());
        }
        if (mRealTimeInfo != null) {
            mAdapter.updateRunningBuses(mRealTimeInfo.getRunningBuses());
        }
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    private void startLoading() {
        Animation animation = AnimationUtils.loadAnimation(BusLineActivity.this, R.anim.rotation);
        mBinding.refresh.clearAnimation();
        mBinding.refresh.startAnimation(animation);
    }

    private void stopLoading() {
        mBinding.refresh.clearAnimation();
    }
}