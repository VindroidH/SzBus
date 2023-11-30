package com.vindroid.szbus.ui.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vindroid.szbus.App;
import com.vindroid.szbus.source.BusCenter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ActivitySearchBinding;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.ui.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, BusCenter.SearchListener {
    private static final String TAG;

    private ActivitySearchBinding mBinding;
    private String mSearchedStr;

    private SearchViewModel mPageViewModel;

    static {
        TAG = App.getTag(SearchActivity.class.getSimpleName());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mBinding.searchView.setOnQueryTextListener(this);
        mBinding.searchView.onActionViewExpanded();

        mPageViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        mBinding.tabs.setVisibility(View.GONE);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query) || query.equals(mSearchedStr)) {
            return false;
        }
        mSearchedStr = query;
        AsyncTask<String, Integer, Boolean> task = new BusCenter.SearchTask(this);
        task.execute(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSearchCompleted(boolean result, SearchResult searchResult, String msg) {
        if (!result) {
            Toast.makeText(this, R.string.cannot_get_data, Toast.LENGTH_SHORT).show();
            mBinding.info.setText(R.string.cannot_get_data);
            mBinding.info.setVisibility(View.VISIBLE);
            mBinding.tabs.setVisibility(View.GONE);
            mBinding.viewPager.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "[onSearchCompleted] type: " + searchResult.getType());
        List<String> tabTitles = new ArrayList<>();
        List<Integer> tabTypes = new ArrayList<>();
        if (searchResult.getType() == SearchResult.Type.None) {
            mBinding.info.setText(R.string.no_data);
            mBinding.info.setVisibility(View.VISIBLE);
            mBinding.tabs.setVisibility(View.GONE);
            mBinding.viewPager.setVisibility(View.GONE);
        } else if (searchResult.getType() == SearchResult.Type.Bus) {
            mBinding.info.setVisibility(View.GONE);
            mBinding.tabs.setVisibility(View.VISIBLE);
            mBinding.viewPager.setVisibility(View.VISIBLE);
            tabTitles.add(getString(R.string.bus_line));
            tabTypes.add(SearchViewModel.TYPE_BUS_LINE);
        } else if (searchResult.getType() == SearchResult.Type.Station) {
            mBinding.info.setVisibility(View.GONE);
            mBinding.tabs.setVisibility(View.VISIBLE);
            mBinding.viewPager.setVisibility(View.VISIBLE);
            tabTitles.add(getString(R.string.station));
            tabTypes.add(SearchViewModel.TYPE_STATION);
        } else if (searchResult.getType() == SearchResult.Type.Both) {
            mBinding.info.setVisibility(View.GONE);
            mBinding.tabs.setVisibility(View.VISIBLE);
            mBinding.viewPager.setVisibility(View.VISIBLE);
            tabTitles.add(getString(R.string.bus_line));
            tabTitles.add(getString(R.string.station));
            tabTypes.add(SearchViewModel.TYPE_BUS_LINE);
            tabTypes.add(SearchViewModel.TYPE_STATION);
        }

        mBinding.tabs.removeAllTabs();
        if (tabTitles.size() > 0) {
            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                    this, initFragmentList(tabTypes));
            mBinding.viewPager.setAdapter(sectionsPagerAdapter);

            for (String title : tabTitles) {
                mBinding.tabs.addTab(mBinding.tabs.newTab().setText(title));
            }
            new TabLayoutMediator(mBinding.tabs, mBinding.viewPager,
                    (tab, position) -> tab.setText(tabTitles.get(position))).attach();
        }

        mPageViewModel.updateBusLineList(searchResult.getBusLines());
        mPageViewModel.updateStationList(searchResult.getStations());
    }

    private List<Fragment> initFragmentList(List<Integer> types) {
        SearchResultFragment fragment1 = SearchResultFragment.newInstance(types.get(0));
        if (types.size() == 1) {
            return Arrays.asList(fragment1);
        }
        SearchResultFragment fragment2 = SearchResultFragment.newInstance(types.get(1));
        return Arrays.asList(fragment1, fragment2);
    }
}
