package com.pplive.liveplatform.ui.home;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.service.live.model.Program;
import com.pplive.liveplatform.ui.widget.AsyncImageView;
import com.pplive.liveplatform.util.DisplayUtil;

public class HomeProgramAdapter extends BaseAdapter {
    private static final DisplayImageOptions DEFAULT_PREVIEW_DISPLAY_OPTIONS = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.program_default_image).showImageForEmptyUri(R.drawable.program_default_image)
            .showStubImage(R.drawable.program_default_image).cacheOnDisc(true).build();

    private static float ratio = 16.0f / 10.0f;

    private List<Program> mPrograms;
    private LayoutInflater mInflater;
    private int mHeight;

    public HomeProgramAdapter(Context context, List<Program> programs) {
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
            convertView = mInflater.inflate(R.layout.layout_program_item, null);
            holder = new ViewHolder();
            holder.previewImageView = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.statusTextView = (TextView) convertView.findViewById(R.id.text_program_status);
            holder.timedownTextView = (TextView) convertView.findViewById(R.id.text_program_timedown);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.ownerTextView = (TextView) convertView.findViewById(R.id.text_program_owner);
            holder.viewcountTextView = (TextView) convertView.findViewById(R.id.text_program_viewcount);
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
        holder.viewcountTextView.setText(String.valueOf(data.getVV()));
        holder.previewImageView.setImageAsync(data.getCoverUrl(), DEFAULT_PREVIEW_DISPLAY_OPTIONS);
    }

    static class ViewHolder {
        AsyncImageView previewImageView;

        TextView statusTextView;

        TextView timedownTextView;

        TextView titleTextView;

        TextView ownerTextView;

        TextView viewcountTextView;
    }
}
