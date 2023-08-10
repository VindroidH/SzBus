package com.vindroid.szbus.ui.choose;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vindroid.szbus.BusCenter;
import com.vindroid.szbus.FavoriteWaiter;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentChooseBusLineBinding;
import com.vindroid.szbus.model.InComingBusLine;
import com.vindroid.szbus.model.StationDetail;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.model.SubscribeBusLine;
import com.vindroid.szbus.ui.station.BusLineListDiffUtil;
import com.vindroid.szbus.utils.Constants;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;

public class ChooseBusLineFragment extends Fragment implements BusCenter.GetStationListener,
        ChooseActivity.Listener {

    private FragmentChooseBusLineBinding mBinding;
    private ChooseBusLineAdapter mAdapter;

    private StationDetail mStationDetail;
    private String mChooseType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mStationDetail = getArguments().getParcelable(Constants.KEY_DATA);
        }
        Activity activity = requireParentFragment().getActivity();
        if (activity instanceof ChooseActivity) {
            mChooseType = ((ChooseActivity) activity).getChooseType();
            ((ChooseActivity) activity).setSubTitle(mStationDetail.getName());
            ((ChooseActivity) activity).addListener(this);
        }
        mBinding = FragmentChooseBusLineBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new ChooseBusLineAdapter(mChooseType);
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));

        AsyncTask<String, Integer, Boolean> task = new BusCenter.GetStation(this);
        task.execute(mStationDetail.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onGetStationCompleted(boolean result, StationDetail station, String msg) {
        mStationDetail = station;
        BusLineListDiffUtil diffUtil = new BusLineListDiffUtil(
                mAdapter.getData(), mStationDetail.getBusLines());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        mAdapter.updateData(mStationDetail.getBusLines());
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    @Override
    public boolean onChooseNext() {
        if (Constants.TYPE_SUBSCRIBE.equals(mChooseType)) {
            if (mAdapter.getSelectedItems().size() == 0) {
                Toast.makeText(requireContext(),
                        requireContext().getString(R.string.choose_at_last_one_bus_line),
                        Toast.LENGTH_LONG).show();
                return false;
            }

            Subscribe subscribe = new Subscribe();
            subscribe.setStation(mStationDetail);
            for (InComingBusLine item : mAdapter.getSelectedItems()) {
                SubscribeBusLine busLine = new SubscribeBusLine(item.getId(), item.getName());
                try {
                    busLine.setAheadOfStation(Integer.parseInt(
                            Objects.requireNonNull(mAdapter.getExtraValues().get(item.getId()))));
                } catch (NumberFormatException | NullPointerException e) {
                    busLine.setAheadOfStation(Constants.DEFAULT_AHEAD);
                }
                subscribe.addBusLine(busLine);
            }

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.KEY_DATA, subscribe);
            NavHostFragment.findNavController(ChooseBusLineFragment.this)
                    .navigate(R.id.action_SecondFragment_to_ThirdFragment);
            return true;
        }
        return false;
    }

    @Override
    public boolean onChooseDone() {
        if (Constants.TYPE_FAVORITE.equals(mChooseType)) {
            if (mAdapter.getSelectedItems().size() == 0) {
                Toast.makeText(requireContext(),
                        requireContext().getString(R.string.choose_at_last_one_bus_line),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            FavoriteWaiter.add(mStationDetail, mAdapter.getSelectedItems());
            return true;
        }
        return false;
    }
}