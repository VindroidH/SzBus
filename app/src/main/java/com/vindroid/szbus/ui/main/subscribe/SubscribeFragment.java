package com.vindroid.szbus.ui.main.subscribe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vindroid.szbus.AdapterListener;
import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentSubscribeBinding;
import com.vindroid.szbus.helper.SubscribeHelper;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.ui.choose.ChooseActivity;
import com.vindroid.szbus.ui.main.favorite.FavoriteFragment;
import com.vindroid.szbus.utils.Constants;

import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class SubscribeFragment extends Fragment implements AdapterListener {
    private static final String TAG;

    private FragmentSubscribeBinding mBinding;
    private SubscribeAdapter mAdapter;

    private List<Subscribe> mSubscribes;

    private final ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadSubscribes();
                    refresh();
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
        mBinding = FragmentSubscribeBinding.inflate(inflater, container, false);

        mAdapter = new SubscribeAdapter(this);
        mBinding.list.setAdapter(mAdapter);

        loadSubscribes();
        refresh();

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onItemClicked(int position, Object obj) {
    }

    @Override
    public void onItemLongClicked(int position, Object obj) {
        final String[] options = new String[]{getString(R.string.delete)};
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.options)).setItems(options, (dialog, which) -> {
                    SubscribeHelper.delete(String.valueOf(obj));
                    loadSubscribes();
                    refresh();
                }).create().show();
    }

    public void addSubscribe() {
        Intent intent = new Intent(requireContext(), ChooseActivity.class);
        intent.putExtra(Constants.KEY_TYPE, Constants.TYPE_SUBSCRIBE);
        mResultLauncher.launch(intent);
    }

    private void loadSubscribes() {
        mSubscribes = SubscribeHelper.getAll();
    }

    private void refresh() {
        SubscribeDiffUtil diffUtil = new SubscribeDiffUtil(mAdapter.getData(), mSubscribes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mAdapter.updateData(mSubscribes);
        diffResult.dispatchUpdatesTo(mAdapter);
    }
}