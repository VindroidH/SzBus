package com.vindroid.szbus;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vindroid.szbus.databinding.ActivityMainBinding;
import com.vindroid.szbus.ui.SectionsPagerAdapter;
import com.vindroid.szbus.ui.main.favorite.FavoriteFragment;
import com.vindroid.szbus.ui.main.subscribe.SubscribeFragment;
import com.vindroid.szbus.ui.search.SearchActivity;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {
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
    }

    private List<Fragment> initFragmentList() {
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        SubscribeFragment subscribeFragment = new SubscribeFragment();
        return Arrays.asList(favoriteFragment, subscribeFragment);
    }

    private void test() {
        // 山塘街 60232c7e-37f5-4336-aafb-aec49478ff2d
        // 406 e82fad29-7847-44c7-971d-9ab98d8f9927

//        AsyncTask<String, Integer, Boolean> task = new BusCenter.SearchTask(null);
//        task.execute("山塘街");

//        AsyncTask<String, Integer, Boolean> task = new BusCenter.SearchTask();
//        task.execute("山塘街");

//        AsyncTask<String, Integer, Boolean> task2 = new BusCenter.GetBus();
//        task2.execute("e82fad29-7847-44c7-971d-9ab98d8f9927");

//        AsyncTask<String, Integer, Boolean> task3 = new BusCenter.GetStation();
//        task3.execute("60232c7e-37f5-4336-aafb-aec49478ff2d");

    }
}