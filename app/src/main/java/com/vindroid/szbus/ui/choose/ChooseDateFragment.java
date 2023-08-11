package com.vindroid.szbus.ui.choose;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentChooseDateBinding;
import com.vindroid.szbus.helper.SubscribeHelper;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.utils.Constants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ChooseDateFragment extends Fragment implements ChooseActivity.Listener {
    private static final String TAG;

    private FragmentChooseDateBinding mBinding;

    private Subscribe mSubscribe;

    static {
        TAG = App.getTag(ChooseDateFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mSubscribe = getArguments().getParcelable(Constants.KEY_DATA);
            Log.d(TAG, "[onCreateView] subscribe: " + mSubscribe.getStation().getName()
                    + ", bus line count: " + mSubscribe.getBusLines().size());
        }

        Activity activity = requireParentFragment().getActivity();
        if (activity instanceof ChooseActivity) {
            ((ChooseActivity) activity).setSubTitle(mSubscribe.getStation().getName());
            ((ChooseActivity) activity).addListener(this);
        }

        mBinding = FragmentChooseDateBinding.inflate(inflater, container, false);
        mBinding.customWeek.setVisibility(mBinding.radioCustomDay.isChecked() ? View.VISIBLE : View.GONE);
        mBinding.dayGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_legal_working_day) {
                mBinding.customWeek.setVisibility(View.GONE);
            } else {
                mBinding.customWeek.setVisibility(View.VISIBLE);
            }
        });

        mBinding.sunday.setOnClickListener(v -> mBinding.sunday.toggle());
        mBinding.monday.setOnClickListener(v -> mBinding.monday.toggle());
        mBinding.tuesday.setOnClickListener(v -> mBinding.tuesday.toggle());
        mBinding.wednesday.setOnClickListener(v -> mBinding.wednesday.toggle());
        mBinding.thursday.setOnClickListener(v -> mBinding.thursday.toggle());
        mBinding.friday.setOnClickListener(v -> mBinding.friday.toggle());
        mBinding.saturday.setOnClickListener(v -> mBinding.saturday.toggle());

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public boolean onChooseNext() {
        return false;
    }

    @Override
    public boolean onChooseDone() {
        String startTime = mBinding.startTime.getText().toString();
        String endTime = mBinding.endTime.getText().toString();
        if (TextUtils.isEmpty(startTime) || startTime.split(":").length != 2
                || TextUtils.isEmpty(endTime) || endTime.split(":").length != 2) {
            Toast.makeText(requireContext(),
                    requireContext().getString(R.string.time_format_error),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        int bit = 0;
        if (mBinding.radioCustomDay.isChecked()) {
            if (mBinding.sunday.isChecked()) bit += 1000000;
            if (mBinding.monday.isChecked()) bit += 100000;
            if (mBinding.tuesday.isChecked()) bit += 10000;
            if (mBinding.wednesday.isChecked()) bit += 1000;
            if (mBinding.thursday.isChecked()) bit += 100;
            if (mBinding.friday.isChecked()) bit += 10;
            if (mBinding.saturday.isChecked()) bit += 1;
        }
        if (bit == 0) {
            Toast.makeText(requireContext(),
                    requireContext().getString(R.string.at_least_one_day),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        mSubscribe.setWeekBit(bit);
        mSubscribe.setStartTime(startTime);
        mSubscribe.setEndTime(endTime);
        SubscribeHelper.add(mSubscribe);
        return true;
    }
}