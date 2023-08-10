package com.vindroid.szbus.ui.main.subscribe;

import com.vindroid.szbus.model.Subscribe;

import java.util.LinkedList;

import androidx.recyclerview.widget.DiffUtil;

public class SubscribeDiffUtil extends DiffUtil.Callback {

    // TODO
    private final LinkedList<Subscribe> mOldList;
    private final LinkedList<Subscribe> mNewList;

    public SubscribeDiffUtil(LinkedList<Subscribe> oldList, LinkedList<Subscribe> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        return false;
    }
}
