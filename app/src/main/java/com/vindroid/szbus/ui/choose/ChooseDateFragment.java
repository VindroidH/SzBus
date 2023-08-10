package com.vindroid.szbus.ui.choose;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.vindroid.szbus.App;
import com.vindroid.szbus.R;
import com.vindroid.szbus.databinding.FragmentChooseDateBinding;
import com.vindroid.szbus.model.Subscribe;
import com.vindroid.szbus.utils.Constants;

import androidx.fragment.app.Fragment;

public class ChooseDateFragment extends Fragment implements View.OnClickListener {
    private static final String TAG;

    private View mRootView;
    private FragmentChooseDateBinding mBinding;

    int mStartTimeHour = 8;
    int mStartTimeMinute = 0;

    int mEndTimeHour = 8;
    int mEndTimeMinute = 30;

    static {
        TAG = App.getTag(ChooseDateFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView != null) {
            return mRootView;
        }
        mBinding = FragmentChooseDateBinding.inflate(inflater, container, false);
        mRootView = mBinding.getRoot();

        if (getArguments() != null) {
            Subscribe subscribe = getArguments().getParcelable(Constants.KEY_DATA);
            Log.d(TAG, "[onCreateView] subscribe: " + subscribe);
        }

        mBinding.selectTime.setOnClickListener(this);
        mBinding.dayGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.legal_working_day) {
                mBinding.customWeek.setVisibility(View.GONE);
            } else {
                mBinding.customWeek.setVisibility(View.VISIBLE);
            }
        });

        return mRootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_time) {
            TimePickerDialog endDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mStartTimeHour = hourOfDay;
                    mStartTimeMinute = minute;
                    mBinding.startTime.setText(requireContext().getString(
                            R.string.time_hh_mm, String.valueOf(hourOfDay), String.valueOf(minute)));
                }
            }, mStartTimeHour, mStartTimeMinute, true);
            endDialog.setTitle(requireContext().getString(R.string.select_end_time));

            TimePickerDialog startDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                mEndTimeHour = hourOfDay;
                mEndTimeMinute = minute;
                mBinding.endTime.setText(requireContext().getString(R.string.time_hh_mm,
                        String.valueOf(hourOfDay), String.valueOf(minute)));
                endDialog.show();
            }, mEndTimeHour, mEndTimeMinute, true);
            startDialog.setTitle(requireContext().getString(R.string.select_start_time));

            startDialog.show();
        }
    }
}