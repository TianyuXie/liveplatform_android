package com.pplive.liveplatform.ui.userpage;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.widget.image.AsyncImageView;

public class UserpageProgramAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<Program> mPrograms;

    private OnItemRightClickListener mRightClickListener;

    public interface OnItemRightClickListener {
        void onRightClick(View v, int position);
    }

    public void setRightClickListener(OnItemRightClickListener l) {
        this.mRightClickListener = l;
    }

    public UserpageProgramAdapter(Context context, List<Program> programs) {
        this.mPrograms = programs;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return null != mPrograms ? mPrograms.size() : 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_userpage_item, parent, false);

            holder = new ViewHolder();
            holder.previewImageView = (AsyncImageView) convertView.findViewById(R.id.image_userpage_program_preview);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_title);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_vv);
            holder.timeTextView = (TextView) convertView.findViewById(R.id.text_userpage_program_time);
            holder.deleteBtn = convertView.findViewById(R.id.btn_userpage_delete_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClickListener != null) {
                    mRightClickListener.onRightClick(v, position);
                }
            }
        });

        updateView(holder, mPrograms.get(position));

        return convertView;
    }

    private void updateView(ViewHolder holder, Program data) {
        holder.titleTextView.setText(data.getTitle());
        holder.previewImageView.setImageAsync(data.getRecommendCover(), R.drawable.program_default_image);
        holder.timeTextView.setText(data.getStartTimeLong());
        switch (data.getLiveStatus()) {
        case LIVING:
            holder.viewcountTextView.setText(String.valueOf(data.getViews()));
            holder.viewcountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.userpage_status_watch, 0, 0, 0);
            break;
        case NOT_START:
        case PREVIEW:
        case INIT:
            holder.viewcountTextView.setText("");
            holder.viewcountTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            break;
        case STOPPED:
            holder.viewcountTextView.setText(String.valueOf(data.getViews()));
            holder.viewcountTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.userpage_status_play, 0, 0, 0);
            break;
        default:
            break;
        }
    }

    static class ViewHolder {
        View deleteBtn;

        AsyncImageView previewImageView;

        TextView timeTextView;

        TextView titleTextView;

        TextView viewcountTextView;
    }
}
