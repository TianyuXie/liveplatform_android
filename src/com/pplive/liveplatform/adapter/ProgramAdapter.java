package com.pplive.liveplatform.adapter;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pplive.android.image.AsyncImageView;
import com.pplive.android.pulltorefresh.RefreshAdapter;
import com.pplive.liveplatform.R;
import com.pplive.liveplatform.core.api.live.model.Program;
import com.pplive.liveplatform.util.DisplayUtil;
import com.pplive.liveplatform.util.TimeHelper;
import com.pplive.liveplatform.util.TimeUtil;

public class ProgramAdapter extends RefreshAdapter<Program> {

    static final String TAG = ProgramAdapter.class.getSimpleName();

    private Context mContext;

    private LayoutInflater mInflater;

    private int mWidth;

    private int mHeight;

    public ProgramAdapter(Context context) {
        Log.d(TAG, "ProgramAdpater");

        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        mWidth = (int) (DisplayUtil.getWidthPx(context) * 0.5);
        mHeight = (int) (mWidth * 3f / 4f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView position: " + position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_program, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.imagePreview = (AsyncImageView) convertView.findViewById(R.id.image_program_preview);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);
            holder.textTitle = (TextView) convertView.findViewById(R.id.text_program_title);
            holder.textTags = (TextView) convertView.findViewById(R.id.text_program_tags);
            holder.textCount = (TextView) convertView.findViewById(R.id.text_program_viewer);
            holder.imageLive = (ImageView) convertView.findViewById(R.id.image_live);

            ViewGroup.LayoutParams lp = holder.imagePreview.getLayoutParams();
            lp.height = mHeight;

            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();

        updateView(holder, position);

        return convertView;
    }

    private void updateView(ViewHolder holder, int position) {

        Program data = getItem(position);

        holder.textTags.setText(data.getTags());
        holder.textTitle.setText(data.getTitle());
        holder.textCount.setText(String.valueOf(data.getViewers()));
        holder.imagePreview.setImageAsync(data.getRecommendCover());

        if (data.isPrelive()) {
            holder.textDate.setVisibility(View.VISIBLE);
            holder.textDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.program_coming_icon, 0, 0, 0);
            holder.textDate.setText(TimeUtil.stringForCountdown(data.getStartTime() - System.currentTimeMillis()));
            holder.imageLive.setVisibility(View.GONE);
        } else if (data.isVOD()) {
            holder.textDate.setVisibility(View.VISIBLE);
            holder.textDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.textDate.setText(TimeHelper.getAboutStartTime(mContext.getResources(), data.getStartTime()));
            holder.imageLive.setVisibility(View.GONE);
        } else {
            holder.imageLive.setVisibility(View.VISIBLE);
            holder.textDate.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {

        AsyncImageView imagePreview;

        TextView textDate;

        TextView textTitle;

        TextView textTags;

        TextView textCount;

        ImageView imageLive;
    }

}
