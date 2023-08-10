package com.vindroid.szbus.ui.main.favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.helper.FavoriteHelper;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentFavoriteBinding;
import com.vindroid.szbus.model.Favorite;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.ui.choose.ChooseActivity;
import com.vindroid.szbus.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;

// TODO add long click to delete, adjust index(up or down)
// TODO display 1st & 2nd bus info
public class FavoriteFragment extends Fragment implements View.OnClickListener, BusCenter.GetStationListener {
    private static final String TAG;

    private FragmentFavoriteBinding mBinding;
    private FavoriteListAdapter mAdapter;

    private LinkedList<Favorite> mFavorites;
    private List<String> mStationIds;
    private boolean mIsRefreshing = false;
    private int syncCount = 0;
    private long mUpdateTimeMills;

    private final ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadFavorite();
                    refreshAll();
                }
            });

    static {
        TAG = App.getTag(FavoriteFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentFavoriteBinding.inflate(inflater, container, false);

        mBinding.refresh.setOnClickListener(this);

        mAdapter = new FavoriteListAdapter();
        mBinding.list.setAdapter(mAdapter);

        loadFavorite();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFavorites.size() > 0
                && System.currentTimeMillis() - mUpdateTimeMills >= Constants.MIN_REFRESH_INTERVAL) {
            refreshAll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.refresh.getId()) {
            if (!mIsRefreshing) {
                refreshAll();
            }
        }
    }

    @Override
    public void onGetStationCompleted(boolean result, StationDetail station, String msg) {
        syncCount--;
        mIsRefreshing = false;
        mUpdateTimeMills = System.currentTimeMillis();
        stopLoading();

        for (Favorite favorite : mFavorites) {
            if (favorite.getStation().getId().equals(station.getId())) {
                for (InComingBusLine info : station.getBusLines()) {
                    favorite.getBusLine(info.getId()).setComing(info.getComing());
                }
                break;
            }
        }

        if (syncCount == 0) {
            FavoriteListDiffUtil diffUtil = new FavoriteListDiffUtil(mAdapter.getData(), mFavorites);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
            mAdapter.updateData(mFavorites);
            diffResult.dispatchUpdatesTo(mAdapter);
        }
    }

    public void addFavorite() {
        Intent intent = new Intent(requireContext(), ChooseActivity.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_FAVORITE);
        mResultLauncher.launch(intent);
    }

    private void loadFavorite() {
        mFavorites = FavoriteHelper.getAll();
        mStationIds = new ArrayList<>();
        for (Favorite favorite : mFavorites) {
            if (!mStationIds.contains(favorite.getStation().getId())) {
                mStationIds.add(favorite.getStation().getId());
            }
        }
    }

    private void refreshAll() {
        mIsRefreshing = true;
        startLoading();
        syncCount = mStationIds.size();
        for (String stationId : mStationIds) {
            AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, stationId);
        }
    }

    private void startLoading() {
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotation);
        mBinding.refresh.clearAnimation();
        mBinding.refresh.startAnimation(animation);
    }

    private void stopLoading() {
        mBinding.refresh.clearAnimation();
    }
}