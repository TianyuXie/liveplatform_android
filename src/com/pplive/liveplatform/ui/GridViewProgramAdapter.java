package com.pplive.liveplatform.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeUtil;

public class GridViewProgramAdapter extends BaseAdapter {

    private static float ratio = 16.0f / 10.0f;

    private List<Program> mPrograms;
    private LayoutInflater mInflater;
    private int mHeight;

    public GridViewProgramAdapter(Context context, List<Program> programs) {
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
            convertView = mInflater.inflate(R.layout.item_program, null);
            holder = new ViewHolder();
            holder.previewImageView = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.timedownTextView = (TextView) convertView.findViewById(R.id.text_date);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.ownerTextView = (TextView) convertView.findViewById(R.id.text_program_tags);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.text_program_viewer);
            holder.liveImageView = (ImageView) convertView.findViewById(R.id.image_live);

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
        holder.ownerTextView.setText(data.getOwnerNickname());
        holder.titleTextView.setText(data.getTitle());
        holder.viewcountTextView.setText(String.valueOf(data.getViewers()));
        holder.previewImageView.setImageAsync(data.getRecommendCover());
        if (data.isPrelive()) {
            holder.timedownTextView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.program_coming_icon, 0, 0, 0);
            holder.timedownTextView.setText(TimeUtil.stringForCountdown(data.getStartTime() - System.currentTimeMillis()));

            holder.liveImageView.setVisibility(View.GONE);
        } else if (data.isVOD()) {
            holder.timedownTextView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.timedownTextView.setText(String.format("%s %s", TimeUtil.stamp2StringShort(data.getRealStartTime()),
                    TimeUtil.stringForTimeMin(data.getLength())));

            holder.liveImageView.setVisibility(View.GONE);
        } else {
            holder.liveImageView.setVisibility(View.VISIBLE);
            holder.timedownTextView.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        AsyncImageView previewImageView;

        TextView timedownTextView;

        TextView titleTextView;

        TextView ownerTextView;

        TextView viewcountTextView;

        ImageView liveImageView;
    }
}
