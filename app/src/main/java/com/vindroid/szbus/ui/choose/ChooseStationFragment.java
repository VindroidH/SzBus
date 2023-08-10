package com.vindroid.szbus.ui.choose;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vindroid.szbus.App;
import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentChooseStationBinding;
import com.vindroid.szbus.model.SearchResult;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.ui.search.SearchStationAdapter;
import com.vindroid.szbus.ui.search.SearchStationDiffUtil;
import com.vindroid.szbus.utils.Constants;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

public class ChooseStationFragment extends Fragment implements SearchView.OnQueryTextListener,
        BusCenter.SearchListener, SearchStationAdapter.OnClickListener {
    private static final String TAG;

    private FragmentChooseStationBinding mBinding;
    private SearchStationAdapter mAdapter;
    private View mRootView;

    private String mSearchedStr;

    static {
        TAG = App.getTag(ChooseStationFragment.class.getSimpleName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            return mRootView;
        }
        mBinding = FragmentChooseStationBinding.inflate(inflater, container, false);
        mAdapter = new SearchStationAdapter(this);
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
        mBinding.search.setOnQueryTextListener(this);
        mBinding.search.onActionViewExpanded();
        mRootView = mBinding.getRoot();
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
        mRootView = null;
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
    public void onSearchCompleted(SearchResult result) {
        if (result.getType() != SearchResult.Type.Both && result.getType() != SearchResult.Type.Station) {
            Log.w(TAG, "[onSearchCompleted] no data");
            return;
        }
        SearchStationDiffUtil diffUtil = new SearchStationDiffUtil(mAdapter.getData(), result.getStations());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mAdapter.updateData(result.getStations());
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    @Override
    public void onItemClick(Object obj) {
        StationDetail detail = (StationDetail) obj;
        Log.d(TAG, "[onItemClick] id: " + detail.getId() + ", name: " + detail.getName());
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_DATA, detail);
        NavHostFragment.findNavController(ChooseStationFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }
}