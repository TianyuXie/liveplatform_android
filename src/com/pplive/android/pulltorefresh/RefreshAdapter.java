package com.pplive.android.pulltorefresh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class RefreshAdapter<T> extends BaseAdapter implements Refreshable<T> {

    private List<T> mData = new ArrayList<T>();

    @Override
    public final void refreshData(Collection<T> data) {
        mData.clear();

        loadData(data);
    }

    @Override
    public final void appendData(Collection<T> data) {
        loadData(data);
    }

    public final void remove(int position) {
        mData.remove(position);

        notifyDataSetChanged();
    }

    private final void loadData(Collection<T> data) {
        mData.addAll(data);

        notifyDataSetChanged();
    }

    @Override
    public final int getCount() {
        return mData.size();
    }

    @Override
    public final T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

}
