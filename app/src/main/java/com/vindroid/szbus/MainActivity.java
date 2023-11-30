package com.vindroid.szbus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vindroid.szbus.databinding.ActivityMainBinding;
import com.vindroid.szbus.service.SubscribeService;
import com.vindroid.szbus.source.BusCenter;
import com.vindroid.szbus.source.DataSource;
import com.vindroid.szbus.ui.SectionsPagerAdapter;
import com.vindroid.szbus.ui.main.favorite.FavoriteFragment;
import com.vindroid.szbus.ui.main.subscribe.SubscribeFragment;
import com.vindroid.szbus.ui.search.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity implements BusCenter.SZXingListener {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, initFragmentList());
        ViewPager2 viewPager = mBinding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        String[] tabTitles = new String[]{
                getString(R.string.favorite), getString(R.string.subscribe)
        };
        for (String title : tabTitles) {
            mBinding.tabs.addTab(mBinding.tabs.newTab().setText(title));
        }
        new TabLayoutMediator(mBinding.tabs, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();

        MaterialToolbar toolbar = mBinding.toolBar;
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_search) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
            } else if (item.getItemId() == R.id.menu_add) {
                if (mBinding.tabs.getSelectedTabPosition() == 0) {
                    FavoriteFragment favoriteFragment = (FavoriteFragment) sectionsPagerAdapter.getFragment(0);
                    favoriteFragment.addFavorite();
                } else {
                    SubscribeFragment subscribeFragment = (SubscribeFragment) sectionsPagerAdapter.getFragment(1);
                    subscribeFragment.addSubscribe();
                }
            }
            return false;
        });

        SubscribeService.startAlarm(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        try {
        String x;
//            String x = new BusApi().getLastUpdateTime();
//            Log.d("SzBus.Test", "last update time: " + x);
//
//            x = new BusApi().getLineInfo("10000110");
//            Log.d("SzBus.Test", "line info: " + x);
//
//            x = new BusApi().getLineDetailInfo("10000110");
//            Log.d("SzBus.Test", "line info: " + x);
//
//            x = new BusApi().getLineRT("10000110");
//            Log.d("SzBus.Test", "line info: " + x);
//
//            x = new BusApi().getStationFile();
//            Log.d("SzBus.Test", "getStationFile: " + x);
//
//            x = new BusApi().getBusLineFile();
//            Log.d("SzBus.Test", "getBusLineFile: " + x);

//        x = new BusApi().getStationRT("10000279");
//        Log.d("SzBus.Test", "getStationRT: " + x);
//        } catch (NetworkErrorException e) {
//            Log.e("SzBus.Test", "NetworkErrorException", e);
//        }
    }

    @Override
    public void onGetVersionCompleted(String versionJson) {
        try {
            JSONObject json = new JSONObject(versionJson);
            String stationTime = json.getJSONObject("station").getString("time");
            String lineTime = json.getJSONObject("line").getString("time");

            JSONObject localJson = new JSONObject(DataSource.getSZXingVersion());
            String localStationTime = localJson.getJSONObject("station").getString("time");
            String localLineTime = localJson.getJSONObject("line").getString("time");

            if (!localStationTime.equals(stationTime)) {
                updateStationFile();
            }
            if (!localLineTime.equals(lineTime)) {
                updateLineFile();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkSZXingVersion() {
        AsyncTask<Void, Integer, String> task = new BusCenter.GetSZXingVersion(this);
        task.execute();
    }

    private void updateStationFile() {
        // TODO
    }

    private void updateLineFile() {
        // TODO
    }

    private List<Fragment> initFragmentList() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        SubscribeFragment subscribeFragment = new SubscribeFragment();
        return Arrays.asList(favoriteFragment, subscribeFragment);
    }
}