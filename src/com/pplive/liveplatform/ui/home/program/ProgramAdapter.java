package com.pplive.liveplatform.ui.home.program;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.rest.Program;
import com.pplive.liveplatform.util.DisplayUtil;

public class ProgramAdapter extends BaseAdapter {
    private static float ratio = 16.0f / 10.0f;

    private List<Program> mPrograms;
    private LayoutInflater mInflater;
    private int mHeight;

    public ProgramAdapter(Context context, List<Program> programs) {
        super();
        this.mPrograms = programs;
        this.mInflater = LayoutInflater.from(context);
        mHeight = (int) (DisplayUtil.getWidthPx(context) / 2.0f / ratio);
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
            convertView = mInflater.inflate(R.layout.layout_program_itemview, null);
            holder = new ViewHolder();
            holder.previewImageView = (ImageView) convertView.findViewById(R.id.imageview_program_preview);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.textview_program_status);
            holder.timedownTextView = (TextView) convertView.findViewById(R.id.textview_program_timedown);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.textview_program_title);
            holder.ownerTextView = (TextView) convertView.findViewById(R.id.textview_program_owner);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.textview_program_viewcount);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        updateView(holder, mPrograms.get(position));
        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        ViewGroup.LayoutParams lp = holder.previewImageView.getLayoutParams();
        lp.height = mHeight;
        holder.ownerTextView.setText(data.getOwner());
        holder.titleTextView.setText(data.getTitle());
    }

    static class ViewHolder {
        ImageView previewImageView;

        TextView statusTextView;

        TextView timedownTextView;

        TextView titleTextView;

        TextView ownerTextView;

        TextView viewcountTextView;
    }
}
