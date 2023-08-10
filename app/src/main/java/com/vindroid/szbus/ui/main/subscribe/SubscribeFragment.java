package com.vindroid.szbus.ui.main.subscribe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vindroid.szbus.App;
import com.vindroid.szbus.databinding.FragmentSubscribeBinding;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.ui.choose.ChooseActivity;
import com.vindroid.szbus.ui.main.favorite.FavoriteFragment;
import com.vindroid.szbus.utils.Constants;

import java.util.LinkedList;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubscribeFragment extends Fragment {
    private static final String TAG;
    private static final String ARG_TYPE = "type";

    private FragmentSubscribeBinding mBinding;
    private SubscribeAdapter mAdapter;

    private LinkedList<Subscribe> mSubscribes;
    private boolean mIsRefreshing = false;
    private int syncCount = 0;

    private final ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                }
            });

    static {
        TAG = App.getTag(FavoriteFragment.class.getSimpleName());
    }

    public static SubscribeFragment newInstance(int index) {
        return new SubscribeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSubscribeBinding.inflate(inflater, container, false);

        mAdapter = new SubscribeAdapter();
        mBinding.list.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void addSubscribe() {
        Intent intent = new Intent(requireContext(), ChooseActivity.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_SUBSCRIBE);
        mResultLauncher.launch(intent);
    }
}