package com.pplive.liveplatform.ui.home;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Program;

public class UserpageProgramAdapter extends BaseAdapter {
    private List<Program> mPrograms;
    private LayoutInflater mInflater;

    public UserpageProgramAdapter(Context context, List<Program> programs) {
        super();
        this.mPrograms = programs;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (null != mPrograms) {
            return mPrograms.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return mPrograms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_userpage_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder {

    }
}
