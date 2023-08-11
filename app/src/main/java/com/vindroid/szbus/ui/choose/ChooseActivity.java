package com.vindroid.szbus.ui.choose;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.ActivityChooseBinding;
import com.vindroid.szbus.utils.Constants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class ChooseActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private static final String TAG;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityChooseBinding mBinding;
    private Listener mListener;

    private String mChooseType;

    private final int MENU_NEXT_ID = 100;
    private final int MENU_DONE_ID = 101;

    interface Listener {
        boolean onChooseNext();

        boolean onChooseDone();
    }

    static {
        TAG = App.getTag(ChooseActivity.class.getSimpleName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityChooseBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mChooseType = getIntent().getStringExtra(Constants.KEY_TYPE);
        Log.d(TAG, "[onCreate] choose type: " + mChooseType);

        setSupportActionBar(mBinding.toolbar);
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_choose);
        navController.addOnDestinationChangedListener((controller, destination, bundle) -> {
            mBinding.toolbar.getMenu().clear();
            if (destination.getId() == R.id.ChooseBusLineFragment) {
                if (Constants.TYPE_SUBSCRIBE.equals(mChooseType)) {
                    mBinding.toolbar.getMenu()
                            .add(Menu.NONE, MENU_NEXT_ID, Menu.NONE, getString(R.string.next))
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {
                    mBinding.toolbar.getMenu()
                            .add(Menu.NONE, MENU_DONE_ID, Menu.NONE, getString(R.string.done))
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
            } else if (destination.getId() == R.id.ChooseDateFragment) {
                mBinding.toolbar.getMenu()
                        .add(Menu.NONE, MENU_DONE_ID, Menu.NONE, getString(R.string.done))
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        mBinding.toolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_choose);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == MENU_NEXT_ID) {
            if (mListener != null) {
                mListener.onChooseNext();
            }
        } else if (item.getItemId() == MENU_DONE_ID) {
            if (mListener != null) {
                if (mListener.onChooseDone()) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        }
        return false;
    }

    public void setSubTitle(String title) {
        mBinding.toolbar.setSubtitle(title);
    }

    public void addListener(Listener listener) {
        mListener = listener;
    }

    public String getChooseType() {
        return mChooseType;
    }
}