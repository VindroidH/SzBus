package com.vindroid.szbus.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vindroid.szbus.App;
import com.vindroid.szbus.databinding.FragmentSearchBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

public class SearchResultFragment extends Fragment {

    private static final String TAG;
    private static final String ARG_TYPE = "type";

    private SearchViewModel mSearchViewModel;
    private FragmentSearchBinding mBinding;
    private SearchBusLineAdapter mBusLineAdapter;
    private SearchStationAdapter mStationAdapter;
    private int mType = SearchViewModel.TYPE_BUS_LINE;

    static {
        TAG = App.getTag(SearchResultFragment.class.getSimpleName());
    }

    public static SearchResultFragment newInstance(int type) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mBinding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        mBinding.list.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));

        if (mType == SearchViewModel.TYPE_BUS_LINE) {
            mBusLineAdapter = new SearchBusLineAdapter();
            mBinding.list.setAdapter(mBusLineAdapter);

            mSearchViewModel.getBusLineList().observe(getViewLifecycleOwner(), busLineList -> {
                Log.d(TAG, "[SearchViewModel] bus line list update, size: " + busLineList.size());
                if (mBusLineAdapter != null) {
                    SearchBusLineDiffUtil diffUtil = new SearchBusLineDiffUtil(mBusLineAdapter.getData(), busLineList);
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffUtil);
                    mBusLineAdapter.updateData(busLineList);
                    result.dispatchUpdatesTo(mBusLineAdapter);
                }
            });
        } else if (mType == SearchViewModel.TYPE_STATION) {
            mStationAdapter = new SearchStationAdapter();
            mBinding.list.setAdapter(mStationAdapter);

            mSearchViewModel.getStationList().observe(getViewLifecycleOwner(), stationList -> {
                Log.d(TAG, "[SearchViewModel] station list update, size: " + stationList.size());
                if (mStationAdapter != null) {
                    SearchStationDiffUtil diffUtil = new SearchStationDiffUtil(mStationAdapter.getData(), stationList);
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffUtil);
                    mStationAdapter.updateData(stationList);
                    result.dispatchUpdatesTo(mStationAdapter);
                }
            });
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}