package com.vindroid.szbus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.vindroid.szbus.databinding.ActivityStationBinding;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.ui.station.BusLineListAdapter;
import com.vindroid.szbus.ui.station.BusLineListDiffUtil;
import com.vindroid.szbus.utils.Constants;
import com.vindroid.szbus.utils.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

public class StationActivity extends AppCompatActivity implements View.OnClickListener,
        BusCenter.GetStationListener {
    private final static String TAG;

    private ActivityStationBinding mBinding;
    private BusLineListAdapter mAdapter;

    private StationDetail mStationDetail = null;
    private boolean mIsRefreshing = false;
    private long mUpdateTimeMills;

    static {
        TAG = App.getTag(StationActivity.class.getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityStationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mStationDetail = new StationDetail();
        mStationDetail.setId(getIntent().getStringExtra(Constants.KEY_ID));
        mStationDetail.setName(getIntent().getStringExtra(Constants.KEY_NAME));
        Log.d(TAG, "[onCreate] " + mStationDetail.getId() + ", " + mStationDetail.getName());

        mBinding.loading.show();

        mBinding.toolbar.setTitle(mStationDetail.getName());
        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mBinding.refresh.setOnClickListener(this);

        mAdapter = new BusLineListAdapter();
        mBinding.busLineList.setAdapter(mAdapter);
        mBinding.busLineList.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() - mUpdateTimeMills >= Constants.MIN_REFRESH_INTERVAL) {
            refreshStationInfo();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mStationDetail = new StationDetail();
        mStationDetail.setId(getIntent().getStringExtra(Constants.KEY_ID));
        mStationDetail.setName(getIntent().getStringExtra(Constants.KEY_NAME));
        Log.d(TAG, "[onNewIntent] " + mStationDetail.getId() + ", " + mStationDetail.getName());

        refreshStationInfo();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.refresh.getId()) {
            if (!mIsRefreshing) {
                refreshStationInfo();
            }
        }
    }

    @Override
    public void onGetStationCompleted(boolean result, StationDetail station, String msg) {
        mIsRefreshing = false;
        mUpdateTimeMills = System.currentTimeMillis();
        stopLoading();
        if (result) {
            mStationDetail = station;
            updateStationStatus();
            updateList();
        }
        mBinding.loading.hide();
        mBinding.contentRoot.setVisibility(View.VISIBLE);
    }

    private void refreshStationInfo() {
        mIsRefreshing = true;
        startLoading();
        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
        task.execute(mStationDetail.getId());
    }

    private void updateStationStatus() {
        mBinding.stationRoot.address.setText(mStationDetail.getAddress());
        mBinding.stationRoot.updateTime.setText(Utils.getTime(Constants.UPDATE_TIME_FORMAT));
    }

    private void updateList() {
        if (mStationDetail == null) return;
        BusLineListDiffUtil diffUtil = new BusLineListDiffUtil(
                mAdapter.getData(), mStationDetail.getBusLines());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mAdapter.updateBusLines(mStationDetail.getBusLines());
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    private void startLoading() {
        Animation animation = AnimationUtils.loadAnimation(StationActivity.this, R.anim.rotation);
        mBinding.refresh.clearAnimation();
        mBinding.refresh.startAnimation(animation);
    }

    private void stopLoading() {
        mBinding.refresh.clearAnimation();
    }
}