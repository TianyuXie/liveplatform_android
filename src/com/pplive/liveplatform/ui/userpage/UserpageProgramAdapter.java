package com.pplive.liveplatform.ui.userpage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.model.Program;

public class UserpageProgramAdapter extends BaseAdapter {
    private List<Program> mPrograms;
    private LayoutInflater mInflater;
    private Context context;

    public UserpageProgramAdapter(Context context, List<Program> programs) {
        super();
        this.context = context;
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
            holder.previewImageView = (ImageView) convertView.findViewById(R.id.image_userpage_program_preview);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_status);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_title);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_vv);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        updateView(holder, mPrograms.get(position));
        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        holder.statusTextView.setText(data.getLiveStatus().toFriendlyString(context));
        holder.titleTextView.setText(data.getTitle());
        holder.timeTextView.setText(data.getStartTimeLong());
    }

    static class ViewHolder {
        ImageView previewImageView;

        TextView statusTextView;

        TextView timeTextView;

        TextView titleTextView;

        TextView viewcountTextView;
    }
}
